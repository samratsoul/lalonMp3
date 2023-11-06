package com.mp3.musicplayer.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.mp3.musicplayer.Utils.Constant;

abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private BroadcastReceiver appCloseReceiver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: BaseActivity");
        if(appCloseReceiver == null) closeApp();
        onSuperCreate();
    }
    public void closeApp(){
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION.CLOSE_APP);
        appCloseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "BaseActivity onReceive: called for finish app");
                finishAffinity();
                System.exit(0);
            }
        };
        registerReceiver(appCloseReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (appCloseReceiver != null) {
            unregisterReceiver(appCloseReceiver);
            appCloseReceiver = null;
        }
        onSuperDestroy();
        super.onDestroy();
    }

    public abstract void onSuperCreate();
    public abstract void onSuperDestroy();
}
