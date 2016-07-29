package com.dwin.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.dwin.dwinapi.SerialPort;

import java.net.URI;
import java.net.URL;
import java.util.Date;
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
    int intCurVideo=0;
    String[] VideoAdd;

    TextView textShow;
//    Thread videThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factivity);
        closeBar();//关闭导航栏Navigation Bar
        setVideoAdd();
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.e("屏幕测试","宽度：" + w_screen + "高度：" + h_screen + "密度：" + dm.densityDpi);

        nameCompant = (TextView) findViewById(R.id.nameCompany);
        arrowFlag = (ImageView) findViewById(R.id.arrowFlag);
        curFloor = (TextView) findViewById(R.id.curFloor);
        adsShow = (VideoView) findViewById(R.id.adsShow);
        textShow = (TextView) findViewById(R.id.textShow);

//        timer = new Timer(true);
//        timer.schedule(task,1000,1000);
        adsShow.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(++intCurVideo==3)
                    intCurVideo=0;
                videoDisplay(VideoAdd[intCurVideo]);
            }
        });
        videoDisplay(VideoAdd[0]);
        openSerialPort();
    }

    void setVideoAdd(){
        VideoAdd = new String[3];
        VideoAdd[0]="/mnt/sdcard1/bell.mp4";
        VideoAdd[1]="/mnt/sdcard1/bell1.mp4";
        VideoAdd[2]="/mnt/sdcard1/bell2.mp4";
    }

    void videoDisplay(String str){
//        Uri videoUri = Uri.parse("/mnt/sdcard1/bell2.mp4");
        Uri videoUri = Uri.parse(str);
        adsShow.setVideoURI(videoUri);
        adsShow.start();
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
    SerialPort serialPort;

    private void openSerialPort(){
        serialPort = new SerialPort("S0", 9600, 8, 1,110);
        new ReceiveThread().start();
    }

    /**
     * 接收数据线程
     */
    class ReceiveThread extends Thread {
        public void run() {
            while (serialPort.isOpen) {
                String type = "ASCII".trim();
                String data = serialPort.receiveData(type);
                if (data != null) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = data;
                    System.out.println(data + "<<<<<<<<==========data");
                    myHandler.sendMessage(msg);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private android.os.Handler myHandler = new android.os.Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Date date = new Date();
                    String test = (String) msg.obj;
                    char[] demo = test.toCharArray();
                    if(demo.length>2){
                        curFloor.setText(new String(demo,0,2));
                        switch (demo[2]){
                            case 'U':arrowFlag.setImageResource(R.drawable.up);break;
                            case 'D':arrowFlag.setImageResource(R.drawable.down);break;
                            default:break;
                        }
//                        curFloor.setText(demo[0]+demo[1]+"");
//                        if(demo[1]>0){
//                            arrowFlag.setImageResource(R.drawable.up);
//                        }else {
//                            arrowFlag.setImageResource(R.drawable.down);
//                        }
                    }
//                    if(demo[0]==0x33){
//                        textShow.setText((CharSequence) msg.obj);
//                    }else{
//                        textShow.setText("数据无效");
//                    }

                    break;
                default:
                    break;
            }
        };
    };
}
