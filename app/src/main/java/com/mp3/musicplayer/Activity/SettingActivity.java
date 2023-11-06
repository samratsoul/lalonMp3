package com.mp3.musicplayer.Activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mp3.musicplayer.Fragments.RateUsDialogFragment;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Services.MusicService;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class SettingActivity extends BaseActivity implements View.OnClickListener {

    boolean serviceBind = false;
    private String[] data;
    private RadioGroup rbGroupLanguage;
    private NumberPicker filterPicker;
    private NumberPicker sleepTimerPicker;
    private String filterData, sleepTimerData;
    private RelativeLayout settingMainHolder, sleep_timer, filter_timer;
    //setting heading and other term
    private TextView settingHeading, settingInviteFriend, filterClip, sleepTimer, language, settingRateUs;
    private TextView doneBtnSleepTimer, cancelBtnSleepTimer, doneBtnFilterTimer, cancelBtnFilterTimer;
    //wheel view container for timer. one timer used for two.
    private Dialog languageDialog;
    private LinearLayout wheelViewContainer;//, languageContainer;
    private final android.os.Handler mHandler = new android.os.Handler();
    private MusicService musicSrv;
    private Intent playPlayIntent;
    private TextView languageHeading;
    private String[] languageArray;
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if (playPlayIntent == null) {
                playPlayIntent = new Intent(SettingActivity.this, MusicService.class);
                playPlayIntent.setAction(Constant.ACTION.STOPFOREGROUND_ACTION);
                bindService(playPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
                serviceBind = true;
                startService(playPlayIntent);
            }

            System.exit(1);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (musicConnection != null && serviceBind) {
                unbindService(musicConnection);
                serviceBind = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuperDestroy() {

    }

    @Override
    public void onSuperCreate() {
        setContentView(R.layout.activity_setting);

        settingMainHolder = (RelativeLayout) findViewById(R.id.setting_main_holder);

        filterPicker = findViewById(R.id.wheel_for_filter);
        sleepTimerPicker = findViewById(R.id.number_picker_sleep);
        data = getApplicationContext().getResources().getStringArray(R.array.timerValue);
        //language dialog for language chose
        languageDialog = new Dialog(this);
        languageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        languageDialog.setContentView(R.layout.language);
        languageHeading = languageDialog.findViewById(R.id.language_heading);
        languageArray = getApplication().getResources().getStringArray(R.array.language);
        rbGroupLanguage = languageDialog.findViewById(R.id.rb_group_language);

        ((RadioButton) rbGroupLanguage.getChildAt(PreferenceManager.getInstance(getApplicationContext()).getLanguagePositionSet())).setChecked(true);

        rbGroupLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_language_1:
                        languageSet(0);
                        break;
                    case R.id.rb_language_2:
                        languageSet(1);
                        break;
                    case R.id.rb_language_3:
                        languageSet(2);
                        break;
                    case R.id.rb_language_4:
                        languageSet(3);
                        break;
                    case R.id.rb_language_5:
                        languageSet(4);
                        break;
                    case R.id.rb_language_6:
                        languageSet(5);
                        break;
                    case R.id.rb_language_7:
                        languageSet(6);
                        break;
                    case R.id.rb_language_8:
                        languageSet(7);
                        break;
                    case R.id.rb_language_9:
                        languageSet(8);
                        break;
                    case R.id.rb_language_10:
                        languageSet(9);
                        break;
                }
            }
        });


        settingHeading = findViewById(R.id.tv_setting_heading);
        settingInviteFriend = findViewById(R.id.tv_setting_invite_friend);
        filterClip = findViewById(R.id.tv_filter_clip);
        sleepTimer = findViewById(R.id.tv_sleep_timer);
        language = findViewById(R.id.tv_language);
        settingRateUs = findViewById(R.id.tv_setting_rate_us);


        settingInviteFriend.setOnClickListener(this);


        language.setOnClickListener(this);
        filterClip.setOnClickListener(this);
        sleepTimer.setOnClickListener(this);


        LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
        View inflatedLayout = inflater.inflate(R.layout.number_picker, null, false);
        settingMainHolder.addView(inflatedLayout);

        wheelViewContainer = findViewById(R.id.ll_wheelView);


        sleep_timer = findViewById(R.id.wheel_timer);
        filter_timer = findViewById(R.id.wheel_filter);

        //btn for sleep timer

        doneBtnFilterTimer = findViewById(R.id.done_btn_sleep);
        cancelBtnSleepTimer = findViewById(R.id.cancel_btn_sleep);
        doneBtnFilterTimer.setOnClickListener(SettingActivity.this);
        cancelBtnSleepTimer.setOnClickListener(SettingActivity.this);

        //btn for filter


        doneBtnSleepTimer = findViewById(R.id.done_btn_filter);
        cancelBtnFilterTimer = findViewById(R.id.cancel_btn_filter);
        doneBtnSleepTimer.setOnClickListener(SettingActivity.this);
        cancelBtnFilterTimer.setOnClickListener(SettingActivity.this);


        filterPicker = findViewById(R.id.wheel_for_filter);
        sleepTimerPicker = findViewById(R.id.number_picker_sleep);
        data = getApplicationContext().getResources().getStringArray(R.array.timerValue);


        filterPicker.setMinValue(0);
        filterPicker.setMaxValue(59);
        filterPicker.setDisplayedValues(data);
        filterPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                filterData = newVal + "";
            }
        });

        sleepTimerPicker.setMinValue(0);
        sleepTimerPicker.setMaxValue(59);
        sleepTimerPicker.setDisplayedValues(data);
        sleepTimerPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sleepTimerData = newVal + "";
            }
        });

        settingRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

       /*
        BitmapDrawable ob = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.main_background), 1, 30));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settingMainHolder.setBackground(ob);
        } else {
            settingMainHolder.setBackgroundResource(R.drawable.main_background);
        }*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();

    }

    private void updateLanguage() {
        languageHeading.setText(getResources().getStringArray(R.array.Language)[Constant.LANGUAGE_POSITION]);
        settingHeading.setText(getResources().getStringArray(R.array.Setting)[Constant.LANGUAGE_POSITION]);
        settingInviteFriend.setText(getResources().getStringArray(R.array.Invite_Friends)[Constant.LANGUAGE_POSITION]);
        filterClip.setText(getResources().getStringArray(R.array.Filter_short_Clips)[Constant.LANGUAGE_POSITION]);
        sleepTimer.setText(getResources().getStringArray(R.array.Sleep_Timer)[Constant.LANGUAGE_POSITION]);
        language.setText(getResources().getStringArray(R.array.Language)[Constant.LANGUAGE_POSITION]);
        settingRateUs.setText(getResources().getStringArray(R.array.Rate_Us)[Constant.LANGUAGE_POSITION]);
        doneBtnFilterTimer.setText(getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        cancelBtnSleepTimer.setText(getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);
        doneBtnSleepTimer.setText(getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        cancelBtnFilterTimer.setText(getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sleep_timer:
                wheelViewContainer.setVisibility(View.VISIBLE);
                sleep_timer.setVisibility(View.VISIBLE);
                break;

            case R.id.tv_filter_clip:
                wheelViewContainer.setVisibility(View.VISIBLE);
                filter_timer.setVisibility(View.VISIBLE);
                break;

            case R.id.cancel_btn_sleep:
                wheelViewContainer.setVisibility(View.GONE);
                sleep_timer.setVisibility(View.GONE);
                break;

            case R.id.done_btn_sleep:
                SimpleDateFormat sdf = new SimpleDateFormat(Constant.SIMPLEDATEFORMATE);
                sdf.setTimeZone(TimeZone.getTimeZone(Constant.TIMEZONE));
                long time = 0;
                try {
                    Date date = new Date();
                    date = sdf.parse(sleepTimerData + ":00");
                    time = date.getTime();
                } catch (Exception e) {

                }
                mHandler.removeCallbacks(mUpdateTimeTask);
                if (time != 0) {
                    mHandler.postDelayed(mUpdateTimeTask, time);
                }
                wheelViewContainer.setVisibility(View.GONE);
                sleep_timer.setVisibility(View.GONE);
                break;
            case R.id.cancel_btn_filter:
                wheelViewContainer.setVisibility(View.GONE);
                filter_timer.setVisibility(View.GONE);
                break;

            case R.id.done_btn_filter:

                Constant.filterTime = filterData;
                PreferenceManager.getInstance(this).setCurrentTime(Constant.filterTime);
                startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;

            case R.id.tv_language:
                //languageContainer.setVisibility(View.VISIBLE);
                languageDialog.show();
                break;


            case R.id.tv_setting_invite_friend:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constant.PLAYSTORELINK + getPackageName());
                startActivity(Intent.createChooser(sharingIntent, Constant.SHARETITTLE));
                break;
        }


    }

    public void languageSet(int position) {
        languageDialog.dismiss();
        Constant.LANGUAGE_POSITION = position;
        PreferenceManager.getInstance(this).setLanguagePositionSet(position);
        Constant.languageChange = true;
        onResume();
    }


    private void showRatingDialog() {
        Log.d("rating", "showForRating : Exception on showing translation dialog");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prevDialog = getSupportFragmentManager().findFragmentByTag(RateUsDialogFragment.TAG);

        if (prevDialog != null) {
            ft.remove(prevDialog);
            Log.d("rating", "showForRating : dialog is showing");
        }
        ft.addToBackStack(null);
        RateUsDialogFragment ratingDialog = RateUsDialogFragment.getInstance(this);
        ratingDialog.show(ft, RateUsDialogFragment.TAG);
    }
}
