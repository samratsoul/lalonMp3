package com.mp3.musicplayer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class CloseAppReceiver extends BroadcastReceiver {
    private static final String TAG = "CloseAppReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: called for finish app");
    }
}
