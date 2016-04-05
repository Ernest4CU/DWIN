package com.dwin.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.dwin.dwinapi.R;

public class FActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factivity);
        DisplayMetrics dm=getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;

        Log.e("屏幕测试","宽度：" + w_screen + "高度：" + h_screen + "密度：" + dm.densityDpi);
    }
}
