package com.shishuheng.iamangery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class GameFragment extends Fragment {

    private final static int ROW = 4;
    private final static int COLUMN = 4;
    public static int sumScore = 0;
    public static int falseCount = 3;
    public boolean LOOP = true;
    private int time = 60;
    public int highestScore;
    public MainActivity parent;

    public Handler loopHandler;
    public Handler timer;

    SoundPool pool;
    int soundID;
    MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup mcontainer,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_game, mcontainer, false);

        player = MediaPlayer.create(getContext(), R.raw.single_dog);

        parent = (MainActivity) getActivity();
        parent.isStart = false;

        falseCount = 3;
        time = 60;
        sumScore = 0;

        pool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
        soundID = pool.load(getContext(), R.raw.hit, 1);
  //      int musicID = pool.load(getContext(), R.raw.single_dog, 2);

//        pool.play(musicID,1,1,0,0,1);

        final TextView scoreview = (TextView) view.findViewById(R.id.scoreview);
        final TextView timeview = (TextView) view.findViewById(R.id.timeview);
        final ImageView falsecountview = (ImageView) view.findViewById(R.id.falsecountview);
        final TextView highestview = (TextView) view.findViewById(R.id.highest);

//        recordHighest("10");
        highestScore = getRecord();

        highestview.setText("最高分: " + highestScore);

        scoreview.setText("分数: " + sumScore);
        timeview.setText(time + " s");
        falsecountview.setImageResource(R.drawable.three_bones);

        final LinearLayout container = (LinearLayout) view.findViewById(R.id.container);
        container.setOrientation(LinearLayout.VERTICAL);
//        final TextView scoreview = (TextView) view.findViewById(R.id.scoreview);
        initGame(parent.getApplicationContext(), container, scoreview, falsecountview, highestview, 4, 4);
        loopHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (!player.isPlaying()) {
                        player.setLooping(true);
                        player.start();
                    }
                    if (sumScore > highestScore) {
                        recordHighest("" + sumScore);
                        highestScore = getRecord();
                        highestview.setText("最高分: " + highestScore);
//                        Log.v("HIGHEST: ", ""+highestScore);
                    }
                    if (time > 0) {
                        container.removeAllViews();
                        initGame(parent.getApplicationContext(), container, scoreview, falsecountview, highestview, 4, 4);
//                        time--;
//                        timeview.setText(time + " s");
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                parent.startFragment = new StartFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.startFragment).commit();
                            }
                        });
                        builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (player.isPlaying()) {
                                    player.stop();
                                    player.release();
                                }
                                parent.gameFragment = new GameFragment();
                                getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.gameFragment).commit();
                            }
                        });
                        String msgtext = "";
                        if (sumScore<30){
                            msgtext = "可怜的单身狗";
                        }else if (sumScore>=30 && sumScore<50) {
                            msgtext = "情侣破坏小能手";
                        }else if (sumScore>=50 && sumScore<80) {
                            msgtext = "分手大师";
                        }else if (sumScore>80){
                            msgtext = "情侣终结者";
                        }
                        builder.setTitle("时间到!");
                        builder.setMessage("你点散了 " + sumScore + " 对恩爱狗！\n恭喜你获得 " + msgtext + " 称号!");
                        builder.setCancelable(false);
                        builder.create().show();
                        LOOP = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        timer = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                time--;
                timeview.setText(time + " s");
                ImageView imageView = (ImageView) view.findViewById(R.id.falsecountview);
                switch (falseCount) {
                    case 1:imageView.setImageResource(R.drawable.one_bone);break;
                    case 2:imageView.setImageResource(R.drawable.two_bones);break;
                    case 3:imageView.setImageResource(R.drawable.three_bones);break;
                    default:imageView.setImageResource(R.drawable.none_bone);
                }
            }
        };

        startThread();

        return view;
    }

    public void recordHighest(String highest) {
        try{
            FileOutputStream fout = parent.openFileOutput("Record", Context.MODE_PRIVATE);
            byte[] bytes = highest.getBytes();
            fout.write(bytes);
            fout.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public int getRecord() {
        String res = "";
        int h = 0;
        try {
            FileInputStream fin = parent.openFileInput("Record");
            int len = fin.available();
            byte[] bytes = new byte[len];
            fin.read(bytes);
            res = new String(bytes);
            h = Integer.parseInt(res);
            fin.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return h;
    }

    public void startThread() {
        LOOP = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (LOOP) {
                    try {
                        Message message = new Message();
                        loopHandler.sendMessage(message);
                        Thread.sleep(1500 - sumScore*10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (LOOP) {
                    try {
                        Message message = new Message();
                        timer.sendMessage(message);
                        Thread.sleep(1000);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void initGame(final Context context, LinearLayout container, final TextView scoreview, final ImageView falsecountview, TextView recordview, int row, int column) {
//        sumScore = 0;

        for (int i = 0; i < row; i++) {
            LinearLayout line = new LinearLayout(context);
            line.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < column; j++) {
                final Unit unit = new Unit(context);

                DisplayMetrics dm = new DisplayMetrics();
                parent.getWindowManager().getDefaultDisplay().getMetrics(dm);
                int size = dm.widthPixels/4;

                unit.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                settingImage(unit, unit.code);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if(unit.code == 1) {
                            sumScore++;
                            scoreview.setText("分数: " + sumScore);
                            unit.code = 4;
                            unit.setImageResource(R.drawable.love_dogs_line_forbid);
                        }else if (unit.code == 2) {
//                            unit.setImageResource(R.drawable.single_dog_line_forbid);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    parent.startFragment = new StartFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.startFragment).commit();
                                }
                            });
                            builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (player.isPlaying()) {
                                        player.stop();
                                        player.release();
                                    }
                                    parent.gameFragment = new GameFragment();
                                    getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.gameFragment).commit();
                                }
                            });
                            builder.setTitle("你输了!");
                            builder.setMessage("单身狗何苦为难单身狗?\n你的总分是: " + sumScore);
                            builder.setCancelable(false);
                            builder.create().show();
                            LOOP = false;
                        }else if (unit.code == 3) {
                            if(falseCount > 0) {
                                falseCount--;
                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        parent.startFragment = new StartFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.startFragment).commit();
                                    }
                                });
                                builder.setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (player.isPlaying()) {
                                            player.stop();
                                            player.release();
                                        }
                                        parent.gameFragment = new GameFragment();
                                        getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.gameFragment).commit();
                                    }
                                });
                                builder.setTitle("游戏已停止!");
                                builder.setMessage("没想到你连基佬都不放过!\n你的总分是: " + sumScore + "\nP.S:组织研究决定给你 肥皂王 称号！");
                                builder.setCancelable(false);
                                builder.create().show();
                                LOOP = false;
                            }
                        }
                        super.handleMessage(msg);
                    }
                };

                unit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Message message = new Message();
                        pool.play(soundID, 1, 1, 0, 0, 1);
                        handler.sendMessage(message);
                    }
                });
                line.addView(unit);
            }
            container.addView(line);
        }
    }
    void settingImage(ImageView iv, int c) {
        switch (c) {
            case 1:iv.setImageResource(R.drawable.love_dogs_line);break;
            case 2:iv.setImageResource(R.drawable.single_dog_line);break;
            case 3:iv.setImageResource(R.drawable.gay_dogs_line);break;
//            case 4:iv.setImageResource(R.drawable.blank_line);break;
            default:iv.setImageResource(R.drawable.blank_line_l);break;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }
}

class Unit extends ImageView {
    //    public int x, y;
    public int code;
    Unit(Context context) {
        super(context);
//        x = p_x;
//        y = p_y;
        code = (int) (1+Math.random()*(4-1+1));
    }
}