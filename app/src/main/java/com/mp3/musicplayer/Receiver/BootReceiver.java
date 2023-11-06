package com.mp3.musicplayer.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mp3.musicplayer.Services.MusicService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent serviceIntent = new Intent(MusicService.class.getName());
            context.startService(serviceIntent);
        } catch (Exception e) {

        }
    }
}
