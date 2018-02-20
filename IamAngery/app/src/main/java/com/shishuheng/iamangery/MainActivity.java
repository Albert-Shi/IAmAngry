package com.shishuheng.iamangery;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public boolean isStart;
    public StartFragment startFragment = null;
    public GameFragment gameFragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getFragmentManager().beginTransaction().replace(R.layout.fragment_game, new GameFragment())
        startFragment = new StartFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, startFragment).commit();
        isStart = true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(gameFragment!=null && gameFragment.isVisible()) {
            gameFragment.LOOP = false;
        }
        if (isStart == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("返回首页", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main, new StartFragment()).commit();
                }
            });
            builder.setNegativeButton("返回游戏", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    gameFragment.startThread();
                }
            });
            builder.setTitle("请选择");
            builder.setMessage("是否退出游戏？");
            builder.setCancelable(false);

            builder.create().show();
        } else {
            System.exit(1);
        }
    }
}