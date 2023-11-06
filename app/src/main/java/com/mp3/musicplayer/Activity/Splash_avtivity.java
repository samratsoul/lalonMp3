package com.mp3.musicplayer.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.PreferenceManager;


public class Splash_avtivity extends AppCompatActivity {
    private static final String TAG = Splash_avtivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 111;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preferenceManager = new PreferenceManager(getApplicationContext());
        Log.d(TAG, "starting activity after 700 ms");
        //permission and get song list
        if (isPrivacyAccepted()) {
            //initial song and layout
            //getAllSong();
            //initial();
            if (checkPermission()) {
                startHomeWithDelay();
            } else {
                //request permission if not permitted

                requestPermission();
            }

        }
    }

    private boolean isPrivacyAccepted() {

        if (!preferenceManager.getFirstTime()) {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                    .setTitle("Need Permission")
                    .setCancelable(false)
                    .setMessage(R.string.privacy_text)

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            preferenceManager.setFirstTime(true);
                            requestPermission();
                        }

                    })

                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            return true;
        }
        return false;
    }


    private void startHomeWithDelay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: starting activity after 700 ms");
                Intent intent = new Intent(Splash_avtivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 400);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WAKE_LOCK, Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);

    }

    private boolean checkPermission() {

        //the seven permission check
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && checkPermission()) {
                    startHomeWithDelay();
                } else {
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        showDialogForRetryPermission();
                    }
                }
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showDialogForRetryPermission() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("All Permission Required !")
                .setMessage("We need all permission to proceed.")
                .setCancelable(false)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission();
                    }
                })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
