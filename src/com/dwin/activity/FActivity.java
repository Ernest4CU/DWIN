package com.dwin.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.dwin.dwinapi.R;

import java.net.URI;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.MemoryHandler;



public class FActivity extends Activity {

    TextView nameCompant;
    Timer timer;
    ImageView arrowFlag;
    TextView curFloor;
    VideoView adsShow;
    int intCurFloor=0;
    Thread videThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factivity);
        closeBar();//关闭导航栏Navigation Bar

        DisplayMetrics dm=getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.e("屏幕测试","宽度：" + w_screen + "高度：" + h_screen + "密度：" + dm.densityDpi);

        nameCompant = (TextView) findViewById(R.id.nameCompany);
        arrowFlag = (ImageView) findViewById(R.id.arrowFlag);
        curFloor = (TextView) findViewById(R.id.curFloor);
        adsShow = (VideoView) findViewById(R.id.adsShow);

        timer = new Timer(true);
        timer.schedule(task,1000,1000);
        videThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri videoUri = Uri.parse("/mnt/sdcard1/bell2.mp4");
                adsShow.setVideoURI(videoUri);
                adsShow.start();
            }
        });
        videThread.start();
    }


    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = new Message();
            mHandler.sendMessage(msg);
        }
    };

    private boolean arrowOrigation = true;
    final android.os.Handler mHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            //nameCompant.setText("测试成功！！");
            //在此执行需要改变的状态
            //首先上下行每秒变化一次
            if(arrowOrigation){
                arrowFlag.setImageResource(R.drawable.down);
                arrowOrigation = false;
            }else {
                arrowFlag.setImageResource(R.drawable.up);
                arrowOrigation = true;
            }
            intCurFloor++;
            if(intCurFloor==50)
                intCurFloor=0;
            curFloor.setText(intCurFloor+"");
            super.handleMessage(msg);
        }
    };


    private void closeBar(){
        try{
            //需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79";

            if(vr.SDK_INT >= vc.ICE_CREAM_SANDWICH){
                ProcID = "42"; //ICS AND NEWER
            }

            //需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[]{"su","-c","service call activity "+ ProcID +" s16 com.android.systemui"}); //WAS 79
            proc.waitFor();

        }catch(Exception ex){
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showBar(){
        try{
            Process proc = Runtime.getRuntime().exec(new String[]{
                    "am","startservice","-n","com.android.systemui/.SystemUIService"});
            proc.waitFor();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
