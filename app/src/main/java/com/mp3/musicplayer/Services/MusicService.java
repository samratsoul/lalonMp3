package com.mp3.musicplayer.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.mp3.musicplayer.Activity.PlayActivity;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "MusicService";
    public static boolean isRunning;
    private final IBinder musicBind = new MusicBinder();
    private final int seekForwardTime = 5000; // 5000 milliseconds
    private final int seekBackwardTime = 5000; // 5000 milliseconds
    private final int PENDING_INTENT_REQUEST_CODE = 1111;
    private final int PREVIOUS_INTENT_REQUEST_CODE = 1112;
    private final int NEXT_INTENT_REQUEST_CODE = 1113;
    private final int PLAY_INTENT_REQUEST_CODE = 1114;
    private final int PAUSE_INTENT_REQUEST_CODE = 1115;
    private final int CLOSE_INTENT_REQUEST_CODE = 1116;
    public Uri trackUri;
    Notification status;
    byte[] art;
    MediaMetadataRetriever metaRetriver;
    private boolean isPlay = false;
    private boolean play;
    //media player
    private MediaPlayer player;
    //song list
    private List<Song> song;
    //current position
    private int songPosn;
    private Equalizer myEq;
    // BassBoost and Virtualizer not currently being implemented
    private BassBoost mBassBoost;
    private Virtualizer mVirtualizer;
    private short maxLevel, minLevel;
    //phone state listhsener
    private PhoneStateListener phoneStateListener;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private boolean isPlayPhone = false;
    private String songTitle = "";
    private AudioManager mAudioManager;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize position
        songPosn = 0;

        //create player
        player = new MediaPlayer();
        initMusicPlayer();
        initEqualizer();
        //pause song if call arrivede
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //INCOMING call
                    //do all necessary action to pause the audio
                    if (player.isPlaying()) {
                        isPlayPhone = true;
                        player.pause();
                        showNotification();
                    }

                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not IN CALL
                    //do anything if the phone-state is idle
                    if (isPlayPhone) {
                        isPlayPhone = false;
                        player.start();
                        showNotification();
                    }
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    //do all necessary action to pause the audio
                    //do something here
                    if (player.isPlaying()) {
                        isPlayPhone = true;
                        player.pause();
                        showNotification();
                    }
                }
                super.onCallStateChanged(state, incomingNumber);
            }


        };

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    try {
                        if (player.isPlaying()) {
                            isPlay = true;
                            player.pause();
                            showNotification();
                        }
                    } catch (Exception e) {

                    }
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    try {
                        if (isPlay) {
                            isPlay = false;
                            player.start();
                            showNotification();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        if (song != null && !song.isEmpty()) {

            if (intent == null) {
                return START_NOT_STICKY;
            }

            try {
                if (Objects.equals(intent.getAction(), Constant.ACTION.STARTFOREGROUND_ACTION)) {
                    //  showNotification();
                    // Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

                } else if (Objects.equals(intent.getAction(), Constant.ACTION.PREV_ACTION)) {
                    //Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
                    //Log.i(LOG_TAG, "Clicked Previous");

                    if (songPosn > 0) {
                        songPosn = songPosn - 1;
                    } else {
                        // play last song
                        songPosn = song.size() - 1;
                    }

                    //Constant.incConstant(this);

                    playSong(true);


                } else if (Objects.equals(intent.getAction(), Constant.ACTION.PLAY_ACTION)) {
                    //Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
                    //Log.i(LOG_TAG, "Clicked Play");

                    if (play) {
                        if (isPlaying()) {
                            Constant.nothingSelected = false;
                            pauseSong();
                        }
                        play = false;
                    } else {
                        play = true;

                        if (!Constant.nothingSelected) {
                            playCurrentPosition();
                        } else {
                            playSong(play);
                        }
                    }


                    showNotification();
                    //stopForeground(true);
                    //stopSelf();

                } else if (Objects.equals(intent.getAction(), Constant.ACTION.NEXT_ACTION)) {

                    if (songPosn < (song.size() - 1)) {
                        songPosn = songPosn + 1;
                    } else {
                        // play first song
                        songPosn = 0;
                    }

                    playSong(true);

                } else if (Objects.equals(intent.getAction(), Constant.ACTION.STOPFOREGROUND_ACTION)) {
                    // Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
                    stopForeground(true);
                    stopSelf();
                    closeNotification();
                    Intent closeIntent = new Intent(Constant.ACTION.CLOSE_APP);
                    sendBroadcast(closeIntent);
                    //System.exit(0);
                    //stopService(new Intent(this,MusicService.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved called from sadikuls music player");
    }

    private void initEqualizer() {
        metaRetriver = new MediaMetadataRetriever();
        player.setAuxEffectSendLevel(1.0f);
        myEq = new Equalizer(10, player.getAudioSessionId());

        // TODO : Implement BassBoost and Virtualizer
        // Setup a "BassBoost" object with priority=10, and audio session id=0 (global)
        mBassBoost = new BassBoost(9, player.getAudioSessionId());
        // Setup a "Virtualizer" object with priority=10, and audio session id=0 (global)
        mVirtualizer = new Virtualizer(8, player.getAudioSessionId());

        // Set up variables for max boost/attenuation for the bands of the equalizer
        maxLevel = myEq.getBandLevelRange()[1];
        minLevel = myEq.getBandLevelRange()[0];
    }

    public void equalizerON() {
        myEq.setEnabled(true);

    }

    public void equalizerOFF() {
        myEq.setEnabled(false);
    }

    public void setEqualizerPreset(short Value) {
        myEq.usePreset(Value);
    }

    public short getCurrentEqualizerPreset() {
        return myEq.getCurrentPreset();
    }

    public short getBandLevelLow() throws RemoteException {
        return minLevel;
    }

    public short getBandLevelHigh() throws RemoteException {
        return maxLevel;
    }

    public int getNumberOfBands() throws RemoteException {
        return myEq.getNumberOfBands();

    }

    public int getCenterFreq(int band) throws RemoteException {
        return myEq.getCenterFreq((short) band);
    }

    public int getBandLevel(int band) throws RemoteException {
        return myEq.getBandLevel((short) band);
    }

    public void setBandLevel(int band, int level) throws RemoteException {
        myEq.setBandLevel((short) band, (short) level);
    }

    public boolean isRunning() throws RemoteException {
        return isRunning;
    }

    public boolean isBassBoostEnabled() throws RemoteException {
        return mBassBoost.getEnabled();
    }

    public void setBassBoostEnabled(boolean isEnabled)
            throws RemoteException {
        mBassBoost.setEnabled(isEnabled);
    }

    public int getBassBoostStrength() throws RemoteException {
        return mBassBoost.getRoundedStrength();
    }

    public void setBassBoostStrength(int strength)
            throws RemoteException {
        mBassBoost.setStrength((short) strength);
    }

    public boolean isVirtualizerEnabled() throws RemoteException {
        return mVirtualizer.getEnabled();
    }

    public void setVirtualizerEnabled(boolean isEnabled)
            throws RemoteException {
        mVirtualizer.setEnabled(isEnabled);
    }

    public int getVirtualizerStrength() throws RemoteException {
        return mVirtualizer.getRoundedStrength();
    }

    public void setVirtualizerStrength(int strength) throws RemoteException {
        mVirtualizer.setStrength((short) strength);
    }

    public void createNotification() {
        showNotification();
    }


    public void closeNotification() {
        //status.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE);

    }

    public void playSong(boolean play) {
        if (song != null && !song.isEmpty()) {
            Constant.incConstant(this);
            int numberOfPlayed = PreferenceManager.getInstance(this).getNumberOfSongPlayedAfterAds();
            numberOfPlayed++;
            PreferenceManager.getInstance(this).setNumberOfSongPlayedAfterAds(numberOfPlayed);
            if (numberOfPlayed > Constant.ADD_INTERVAL) {
                PreferenceManager.getInstance(this).setShowADS(true);
            }
            //play a song
            player.reset();
            //get song
            Song playSong = song.get(songPosn);
            songTitle = playSong.getTitle();
            //get id
            long currSong = playSong.getId();
            //set uri
            trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
            try {
                player.setDataSource(getApplicationContext(), trackUri);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MUSIC SERVICE", "Error setting data source", e);
            }

            this.play = play;
            player.prepareAsync();
            showNotification();
        }

    }

    public Uri trackUri() {
        return trackUri;
    }

    public void setList(List<Song> theSongs) {
        song = theSongs;
    }

    public List<Song> getSongList() {
        return song;
    }

    public int getSongPosn() {
        return songPosn;
    }

    public void mutePlayer() {
        player.setVolume(0, 0);
    }

    public void unMuteVolume() {
        player.setVolume(1, 1);
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;

    }

    public void pauseSong() {
        player.pause();
        showNotification();
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    public long getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public boolean isLoping() {
        return player.isLooping();
    }

    public void seekTo(int currentPosition) {
        player.seekTo(currentPosition);

    }

    public long getCurrentID() {
        try {
            return song.get(songPosn).getId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
/*
    public void playForward() {
        int currentPosition = player.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (player.isPlaying()) {
            if (currentPosition + seekForwardTime <= player.getDuration()) {
                // forward song
                player.seekTo(currentPosition + seekForwardTime);
            } else {
                // forward to end position
                player.seekTo(player.getDuration());
            }
        }
    }*/
/*
    public void playBackword() {
        int currentPosition = player.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if (player.isPlaying()) {
            if (currentPosition - seekBackwardTime >= 0) {
                // forward song
                player.seekTo(currentPosition - seekBackwardTime);
            } else {
                // backward to starting position
                player.seekTo(0);
            }
        }
    }*/

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public void playCurrentPosition() {
        player.start();
        showNotification();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build());
        }

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //player.stop();
        //player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
// check for repeat is ON or OFF
        if (song != null && !song.isEmpty()) {

            player.reset();
            Constant.nothingSelected = false;
            if (Constant.reapeatAll == 1) {
                // repeat is on play same song again
                playSong(true);
            } else if (Constant.reapeatAll == 2) {
                // no repeat or shuffle ON - play next song
                if (songPosn < (song.size() - 1)) {
                    songPosn = songPosn + 1;
                    playSong(true);

                } else {
                    // play first song
                    songPosn = 0;
                    playSong(true);

                }
            } else if (isShuffle) {
                // shuffle is on - play a random song
                Random rand = new Random();
                songPosn = rand.nextInt((song.size() - 1) - 0 + 1) + 0;
                playSong(true);
            } else {
                Constant.nothingSelected = true;
            }
            showNotification();


        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("MUSIC_PLAYER", "Playback Error");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (play && !player.isPlaying()) {
                    player.reset();
                }
            }
        }, 2000);

        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v("MUSIC_PLAYER", "Playback prepared");
        if (play) {
            player.start();
        }
        //myEq.setEnabled(true);
        showNotification();


    }

    @Override
    public void onDestroy() {
        myEq.release();
        player.release();
        mBassBoost.release();
        mVirtualizer.release();
        // Set myEq pointer to null
        myEq = null;
        mBassBoost = null;
        mVirtualizer = null;
        isRunning = false;
        phoneStateListener = null;
        stopForeground(true);
    }

    private void showNotification() {


        if (song != null && !song.isEmpty()) {

            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.notification_bar_small);
            RemoteViews bigViews = new RemoteViews(getPackageName(),
                    R.layout.notification_bar);


            try {
                metaRetriver.setDataSource(this, trackUri);
                art = metaRetriver.getEmbeddedPicture();
                if (art != null) {
                    Bitmap shareImageRow1 = BitmapFactory.decodeByteArray(art, 0, art.length);
                    if (shareImageRow1 != null) {
                        bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.color.null_color);
                        views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.color.null_color);
                        views.setImageViewBitmap(R.id.status_bar_album_art, shareImageRow1);
                        bigViews.setImageViewBitmap(R.id.status_bar_album_art, shareImageRow1);

                    } //else {
                    // views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image);
                    // bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image);
                    // }
                } else {

                    views.setImageViewResource(R.id.status_bar_album_art, R.color.null_color);
                    bigViews.setImageViewResource(R.id.status_bar_album_art, R.color.null_color);

                    switch (PreferenceManager.getInstance(this).getImageConstant()) {
                        case 0:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image);
                            break;
                        case 1:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image1);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image1);
                            break;
                        case 2:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image2);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image2);
                            break;
                        case 3:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image3);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image3);
                            break;
                        case 4:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image4);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image4);
                            break;
                        case 5:
                            bigViews.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image5);
                            views.setInt(R.id.status_bar_album_art, "setBackgroundResource", R.drawable.round_image5);
                            break;
                    }

                }
            } catch (Exception e) {
                //views.setImageViewResource(R.id.status_bar_album_art, R.drawable.round_image);
                //bigViews.setImageViewResource(R.id.status_bar_album_art, R.drawable.round_image);
            }


            Intent notificationIntent = new Intent(this, PlayActivity.class);
            //notificationIntent.setAction(Constant.ACTION.MAIN_ACTION);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, PENDING_INTENT_REQUEST_CODE,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent previousIntent = new Intent(this, MusicService.class);
            previousIntent.setAction(Constant.ACTION.PREV_ACTION);
            PendingIntent ppreviousIntent = PendingIntent.getService(this, PREVIOUS_INTENT_REQUEST_CODE,
                    previousIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent playIntent = new Intent(this, MusicService.class);
            playIntent.setAction(Constant.ACTION.PLAY_ACTION);
            PendingIntent pplayIntent = PendingIntent.getService(this, PLAY_INTENT_REQUEST_CODE,
                    playIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent nextIntent = new Intent(this, MusicService.class);
            nextIntent.setAction(Constant.ACTION.NEXT_ACTION);
            PendingIntent pnextIntent = PendingIntent.getService(this, NEXT_INTENT_REQUEST_CODE,
                    nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            Intent closeIntent = new Intent(this, MusicService.class);
            closeIntent.setAction(Constant.ACTION.STOPFOREGROUND_ACTION);
            PendingIntent pcloseIntent = PendingIntent.getService(this, CLOSE_INTENT_REQUEST_CODE,
                    closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

            views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

            views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
            bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

            views.setOnClickPendingIntent(R.id.ivCloseNotification, pcloseIntent);
            bigViews.setOnClickPendingIntent(R.id.ivCloseNotification, pcloseIntent);

            if (player.isPlaying()) {
                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.pause_notification);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.pause_notification);
            } else {
                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.play_with_circle_notification);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.play_with_circle_notification);
            }

            // views.setTextViewText(R.id.status_bar_track_name, song.get(songPosn).getTitle());
            bigViews.setTextViewText(R.id.status_bar_track_name, song.get(songPosn).getTitle());
            // views.setTextViewText(R.id.status_bar_artist_name,song.get(songPosn).getArtist());
            bigViews.setTextViewText(R.id.status_bar_artist_name, song.get(songPosn).getArtist());
            bigViews.setTextViewText(R.id.status_bar_album_name, song.get(songPosn).getAlbum());

            Notification.Builder builder = new Notification.Builder(this);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            builder.setPriority(Notification.PRIORITY_DEFAULT);
            if (SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_SECRET);//to show content in lock screen
            }
            if (SDK_INT >= O) {
                builder.setChannelId(Constant.CHANNEL_ID);
            }
            status = builder.build();
            status.contentView = views;
            status.bigContentView = bigViews;
            // status.flags =Notification.FLAG_AUTO_CANCEL;

            status.icon = R.drawable.play_normal;
            status.contentIntent = pendingIntent;

            // startForeground(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Will display the notification in the notification bar
            assert notificationManager != null;
            notificationManager.notify(Constant.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
        }
    }


    // notificaiton creation function

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
}


