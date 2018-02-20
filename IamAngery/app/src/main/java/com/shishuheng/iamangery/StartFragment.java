package com.shishuheng.iamangery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StartFragment extends Fragment {
    MainActivity parent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        parent = (MainActivity) getActivity();
        parent.isStart = true;
        if (parent.gameFragment!=null && parent.gameFragment.player.isPlaying()) {
            parent.gameFragment.player.stop();
            parent.gameFragment.player.release();
        }
        ImageView button = (ImageView) view.findViewById(R.id.startbutton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.gameFragment = new GameFragment();
                getFragmentManager().beginTransaction().replace(R.id.activity_main, parent.gameFragment).commit();
            }
        });

        TextView htp = (TextView) view.findViewById(R.id.help);
        htp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setPositiveButton("我知道啦!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setTitle("怎么玩?");
                builder.setMessage("游戏上方中间为当前分数,左上角是历史最高分,右上角是游戏剩余时间(总时间为60s),时间下方骨头代表还可以点击基佬狗的次数.\n游戏中会出现三种狗\n1.一只狗：代表单身狗.单身狗是保护动物,我们应该爱他,千万不能点!\n2.两只狗(没有花)：代表一对基佬狗.基佬狗有其搞基的自由,看不惯可以点,但只能点三次(支持同性恋是政治正确2333),屏幕左上角 骨头 代表目前能点的次数.骨头没有了再点就会GG!\n3.两只狗(有花)：代表一对恩爱狗,尽情的去点击拆散他们吧!\nP.S.分数越高，doge闪的越快\n\n*11.11更新*\nP.P.S 本次更新加了音效跟BGM。本来光棍节想做一个新的游戏，但是有这个想法的时候已经是10号了时间不够了，只能作罢，只能等下次了-_-#");
                builder.create().show();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        System.exit(1);
    }
}