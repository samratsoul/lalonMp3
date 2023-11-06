package com.mp3.musicplayer.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mp3.musicplayer.CustomView.CircularSeekBar;
import com.mp3.musicplayer.CustomView.VerticalSeekBar;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Services.MusicService;
import com.mp3.musicplayer.Utils.BlurFilter;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;

import java.util.HashMap;

public class EqualizerActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {


    BlurFilter blurFilter = new BlurFilter();
    //spinner for preset
    Spinner PresetSpinner;
    //vertical seekbar for 5 band
    private VerticalSeekBar band1, band2, band3, band4, band5;
    // linear layout for bass virtualizer nob holder
    private LinearLayout bass_bakground, virtualizer_background, equalizerBody;
    // the main background
    private RelativeLayout main_bakgrouund;
    //on off equalizer button
    private ImageButton onoff;
    // preset name string array
    private String[] PresetName;
    private int PresetNumber;
    // music service for control player equalizer and bass effect
    private MusicService musicSrv;
    private Intent playPlayIntent;
    private boolean serviceBind = false;
    //
    // preference for store value
    //circualr bar for bass and virtualizer
    private CircularSeekBar BassControlBar, VirtualizerControlBar;
    // equalizer heading and freq mid value text view
    private TextView freq_text1, freq_text2, freq_text3, freq_text4, freq_text5, equalizer_heading;
    private short lowerEqualizerBandLevel = 0;
    private short upperEqualizerBandLevel = 0;
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();

            //create ui if service is connected
            creatUI();
            //Toast.makeText(Equalizer.this, "Service connected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    public void onSuperDestroy() {

    }

    @Override
    public void onSuperCreate() {
        setContentView(R.layout.activity_equalizer);
        //a sample media player and equalizer for preset value and band info
        MediaPlayer TestMedia = new MediaPlayer();
        android.media.audiofx.Equalizer equalizer = new android.media.audiofx.Equalizer(5, TestMedia.getAudioSessionId());
        //initialization
        freq_text1 = findViewById(R.id.freq_band1);
        freq_text2 = findViewById(R.id.freq_band2);
        freq_text3 = findViewById(R.id.freq_band3);
        freq_text4 = findViewById(R.id.freq_band4);
        freq_text5 = findViewById(R.id.freq_band5);

        equalizer_heading = findViewById(R.id.equalizer_heading);

        equalizerBody = findViewById(R.id.equalizer_body);

        bass_bakground = findViewById(R.id.ll_bass_bakground);
        virtualizer_background = findViewById(R.id.ll_virtualizer_bakground);

        PresetSpinner = findViewById(R.id.eq_spinner);
        //create 1 extra preset for custom band
        PresetNumber = equalizer.getNumberOfPresets() + 1;
        PresetName = new String[PresetNumber];
        //name first one the custom band
        PresetName[0] = Constant.CUSTOMEQUALIZERNAME;

        //take the preset value
        for (int i = 1; i < PresetNumber; i++) {
            PresetName[i] = equalizer.getPresetName((short) (i - 1));
        }

        //initial the band mid frequency

        freq_text1.setText(equalizer.getCenterFreq((short) 0) / 1000 + " Hz");
        freq_text2.setText(equalizer.getCenterFreq((short) 1) / 1000 + " Hz");
        freq_text3.setText(equalizer.getCenterFreq((short) 2) / 1000 + " Hz");
        freq_text4.setText(equalizer.getCenterFreq((short) 3) / 1000 + " Hz");
        freq_text5.setText(equalizer.getCenterFreq((short) 4) / 1000 + " Hz");

        //relese test eq and player
        equalizer.release();
        TestMedia.release();

        // spinner value assign with custom layout dropdown
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, PresetName);
        adapter1.setDropDownViewResource(R.layout.dropdown);


        PresetSpinner.setAdapter(adapter1);
        PresetSpinner.setOnItemSelectedListener(this);


        BassControlBar = findViewById(R.id.bass_bar_control);
        VirtualizerControlBar = findViewById(R.id.virtualizer_bar_control);

        //max 1000 value
        BassControlBar.setMax(1000);
        VirtualizerControlBar.setMax(1000);

        main_bakgrouund = (RelativeLayout) findViewById(R.id.equalizer_main_background);
        onoff = (ImageButton) findViewById(R.id.equalizer_on_off_switch);

        onoff.setOnClickListener(this);

        band1 = findViewById(R.id.eq_band1);
        band2 = findViewById(R.id.eq_band2);
        band3 = findViewById(R.id.eq_band3);
        band4 = findViewById(R.id.eq_band4);
        band5 = findViewById(R.id.eq_band5);

/*
        //add background blur image
        BitmapDrawable ob = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.main_background), 1, 30));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            main_bakgrouund.setBackground(ob);
        } else {
            main_bakgrouund.setBackgroundResource(R.drawable.main_background);
        }*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        equalizer_heading.setText(getResources().getStringArray(R.array.Equalizer)[Constant.LANGUAGE_POSITION]);

    }

    public void creatUI() {


        //bass bar control and save current value on progress change
        BassControlBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                try {

                    if (musicSrv != null && progress != 0) {
                        musicSrv.setBassBoostEnabled(false);
                        musicSrv.setBassBoostStrength(progress);
                        musicSrv.setBassBoostEnabled(true);
                        PreferenceManager.getInstance(getApplicationContext()).setBaseProgress(progress);
                        PreferenceManager.getInstance(getApplicationContext()).setBase(true);
                    }


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                bass_bakground.setBackgroundResource(R.drawable.bass_not_selected);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

                bass_bakground.setBackgroundResource(R.drawable.bass_selected);

            }
        });


        //virtualizer bar control and save current value on progress change
        VirtualizerControlBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                try {
                    if (musicSrv != null && progress != 0) {
                        musicSrv.setVirtualizerEnabled(false);
                        musicSrv.setVirtualizerStrength(progress);
                        musicSrv.setVirtualizerEnabled(true);
                        PreferenceManager.getInstance(getApplicationContext()).setVirtualizerProgress(progress);
                        PreferenceManager.getInstance(getApplicationContext()).setVirtualizer(true);
                    }


                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                virtualizer_background.setBackgroundResource(R.drawable.virtualizer_not_selected);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {
                virtualizer_background.setBackgroundResource(R.drawable.virtualizer_selected);
            }
        });


        //taking equalizer preset value last saved and other value
        int equalizerPreset = PreferenceManager.getInstance(getApplicationContext()).getEqualizerPreset();
        int bassProgress = PreferenceManager.getInstance(getApplicationContext()).getBaseProgress();
        int virtualizerProgress = PreferenceManager.getInstance(getApplicationContext()).getVirtualizerProgress();
        Constant.equalizerON = PreferenceManager.getInstance(getApplicationContext()).getEqualizer();

        //initialize the equalizer as saved value
        try {
            if (musicSrv != null) {
                VirtualizerControlBar.setProgress(virtualizerProgress);
                musicSrv.setVirtualizerEnabled(true);
                musicSrv.setVirtualizerStrength(virtualizerProgress);
                BassControlBar.setProgress(bassProgress);
                musicSrv.setBassBoostEnabled(true);
                musicSrv.setBassBoostStrength(bassProgress);

                PresetSpinner.setSelection(equalizerPreset);

                lowerEqualizerBandLevel = musicSrv.getBandLevelLow();
                upperEqualizerBandLevel = musicSrv.getBandLevelHigh();

                band1.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                band2.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                band3.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                band4.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
                band5.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);

                if (equalizerPreset == 0) {
                    cutomEqualizerValue();
                } else {
                    musicSrv.setEqualizerPreset((short) (equalizerPreset - 1));
                }

                if (Constant.equalizerON) {
                    musicSrv.equalizerON();
                    equalizerBody.setAlpha(1);
                    setViewAndChildrenEnabled(equalizerBody, true);
                } else {
                    musicSrv.equalizerOFF();
                    equalizerBody.setAlpha(.3f);
                    setViewAndChildrenEnabled(equalizerBody, false);
                }

                //Toast.makeText(this,musicSrv.getNumberOfBands()+"",Toast.LENGTH_SHORT).show();
                if (Constant.equalizerON) {
                    band1.setProgressAndThumb((musicSrv.getBandLevel(0)) - lowerEqualizerBandLevel);
                    band2.setProgressAndThumb((musicSrv.getBandLevel(1)) - lowerEqualizerBandLevel);
                    band3.setProgressAndThumb((musicSrv.getBandLevel(2)) - lowerEqualizerBandLevel);
                    band4.setProgressAndThumb((musicSrv.getBandLevel(3)) - lowerEqualizerBandLevel);
                    band5.setProgressAndThumb((musicSrv.getBandLevel(4)) - lowerEqualizerBandLevel);
                } else {
                    band1.setProgressAndThumb((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
                    band2.setProgressAndThumb((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
                    band3.setProgressAndThumb((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
                    band4.setProgressAndThumb((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
                    band5.setProgressAndThumb((upperEqualizerBandLevel - lowerEqualizerBandLevel) / 2);
                }
                band1.setOnSeekBarChangeListener(this);
                band2.setOnSeekBarChangeListener(this);
                band3.setOnSeekBarChangeListener(this);
                band4.setOnSeekBarChangeListener(this);
                band5.setOnSeekBarChangeListener(this);

            }
        } catch (RemoteException e) {

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playPlayIntent == null) {
            playPlayIntent = new Intent(this, MusicService.class);
            playPlayIntent.setAction(Constant.ACTION.STARTFOREGROUND_ACTION);
            bindService(playPlayIntent, musicConnection, Context.BIND_AUTO_CREATE);
            serviceBind = true;
            startService(playPlayIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (musicConnection != null && serviceBind) {
                unbindService(musicConnection);
                serviceBind=false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        switch (seekBar.getId()) {
            case R.id.eq_band1:
                try {
                    if (musicSrv != null)
                        musicSrv.setBandLevel(0, (short) (progress + lowerEqualizerBandLevel));
                } catch (Exception e) {

                }
                break;
            case R.id.eq_band2:
                try {
                    if (musicSrv != null)
                        musicSrv.setBandLevel(1, (short) (progress + lowerEqualizerBandLevel));
                } catch (Exception e) {

                }
                break;
            case R.id.eq_band3:
                try {
                    if (musicSrv != null)
                        musicSrv.setBandLevel(2, (short) (progress + lowerEqualizerBandLevel));
                } catch (Exception e) {

                }
                break;
            case R.id.eq_band4:
                try {
                    if (musicSrv != null)
                        musicSrv.setBandLevel(3, (short) (progress + lowerEqualizerBandLevel));
                } catch (Exception e) {

                }
                break;
            case R.id.eq_band5:
                try {
                    if (musicSrv != null)
                        musicSrv.setBandLevel(4, (short) (progress + lowerEqualizerBandLevel));
                } catch (Exception e) {

                }
                break;
        }
        //save band if change and goto custom because only custom slection can change vlaue
        saveBand();
        PresetSpinner.setSelection(0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.equalizer_on_off_switch:
                if (Constant.equalizerON) {
                    Constant.equalizerON = false;
                    if (musicSrv != null) musicSrv.equalizerOFF();
                    equalizerBody.setAlpha(.3f);
                    setViewAndChildrenEnabled(equalizerBody, false);
                    Toast.makeText(this, Constant.EQUALIZER_OFF, Toast.LENGTH_SHORT).show();
                } else {
                    if (musicSrv != null) musicSrv.equalizerON();
                    Toast.makeText(this, Constant.EQUALIZER_ON, Toast.LENGTH_SHORT).show();
                    Constant.equalizerON = true;
                    equalizerBody.setAlpha(1);
                    setViewAndChildrenEnabled(equalizerBody, true);
                }
                PreferenceManager.getInstance(getApplicationContext()).setEqualizer(Constant.equalizerON);
                creatUI();
                break;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.eq_spinner:
                if (musicSrv != null && musicSrv.isPlaying()) {
                    if (position == 0) {
                        //custom funciton for custom equalizer
                        cutomEqualizerValue();
                    } else {
                        //-1 for one extra custom value
                        musicSrv.setEqualizerPreset((short) (position - 1));
                    }


                    //update progress
                    try {
                        if (Constant.equalizerON) {
                            band1.setProgressAndThumb((musicSrv.getBandLevel(0)) - lowerEqualizerBandLevel);
                            band2.setProgressAndThumb((musicSrv.getBandLevel(1)) - lowerEqualizerBandLevel);
                            band3.setProgressAndThumb((musicSrv.getBandLevel(2)) - lowerEqualizerBandLevel);
                            band4.setProgressAndThumb((musicSrv.getBandLevel(3)) - lowerEqualizerBandLevel);
                            band5.setProgressAndThumb((musicSrv.getBandLevel(4)) - lowerEqualizerBandLevel);
                        }
                        //saveBand();

                    } catch (Exception e) {

                    }
                }

                PreferenceManager.getInstance(getApplicationContext()).setEqualizerPreset(position);
                break;
        }
    }


    private void saveBand() {

        //save 5 band value
        try {
            if (musicSrv != null) {
                HashMap<String, Integer> band = new HashMap<>();
                band.put("BAND1", (musicSrv.getBandLevel(0)) - lowerEqualizerBandLevel);
                band.put("BAND2", (musicSrv.getBandLevel(1)) - lowerEqualizerBandLevel);
                band.put("BAND3", (musicSrv.getBandLevel(2)) - lowerEqualizerBandLevel);
                band.put("BAND4", (musicSrv.getBandLevel(3)) - lowerEqualizerBandLevel);
                band.put("BAND5", (musicSrv.getBandLevel(4)) - lowerEqualizerBandLevel);
                PreferenceManager.getInstance(getApplicationContext()).setEqualizerBand(band);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cutomEqualizerValue() {
        int band_eq1, band_eq2, band_eq3, band_eq4, band_eq5;

        try {

            HashMap<String, Integer> band = PreferenceManager.getInstance(getApplicationContext()).getEqualizerBand();
            band_eq1 = band.get("BAND1");
            band_eq2 = band.get("BAND2");
            band_eq3 = band.get("BAND3");
            band_eq4 = band.get("BAND4");
            band_eq5 = band.get("BAND5");

            if (musicSrv != null) {

                musicSrv.setBandLevel(0, (short) (band_eq1 + lowerEqualizerBandLevel));
                musicSrv.setBandLevel(1, (short) (band_eq2 + lowerEqualizerBandLevel));
                musicSrv.setBandLevel(2, (short) (band_eq3 + lowerEqualizerBandLevel));
                musicSrv.setBandLevel(3, (short) (band_eq4 + lowerEqualizerBandLevel));
                musicSrv.setBandLevel(4, (short) (band_eq5 + lowerEqualizerBandLevel));

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
