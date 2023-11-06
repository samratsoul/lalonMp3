package com.mp3.musicplayer.Activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
import com.mp3.musicplayer.Adapters.SongAdapter;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Services.MusicService;
import com.mp3.musicplayer.Utils.BlurFilter;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;
import com.mp3.musicplayer.Utils.Utilities;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mp3.musicplayer.R.string.addedToPlaylist;

public class PlayActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, SongAdapter.ClickListhener {

    private static final int RESULT_LOAD_IMAGE = 555;
    private final Handler mHandler = new Handler();
    public long old_ID = 0;
    //some flag for playlist
    public boolean isPLayList = false;
    public String playListName = "";
    SongAdapter songAdapter;
    //for take album art
    byte[] art;
    BlurFilter blurFilter = new BlurFilter();
    boolean serviceBind = false;
    private boolean playFromFile = false;
    //gson for class save like playlist
    private int ofset = 0;
    //music service for control player
    private MusicService musicSrv;
    private Intent playPlayIntent;
    private TextView btnShareShare, btnShareCancel;
    private ImageButton btnPlay, btnNext, btnPrevious, btnSuffle, btnReapeat, btnPlaylist, btnPlayEqualizer, btnAddToPlaylist;
    private Button playlistCancel;
    //playlist adapter
    private ArtistGenrsAlbumAdapter playListMenuAdapter;
    //for album art share
    private CircleImageView shareImage;
    //playbutton holder linear layout
    private EditText shareText;
    private LinearLayout shareContent;//, playTopView;
    private Dialog playListDialog;
    private boolean play = true;
    private List<Song> songList;
    //for playlist
    private RecyclerView songView, plaListView;
    //current song infor textview
    private TextView songTitleLabel, songArtist, songCurrentDurationLabel, songTotalDurationLabel;
    //for album art
    private ImageView album_art;
    //song progress
    private SeekBar songProgressBar;
    private ConstraintLayout main_view;
    //for take album art
    private MediaMetadataRetriever metaRetriver;
    private Utilities utils;
    private int currentSongIndex = 0;
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (musicSrv != null) {
                    currentSongIndex = musicSrv.getSongPosn();
                    long totalDuration = musicSrv.getDuration();
                    long currentDuration = musicSrv.getCurrentPosition();

                    boolean update = (totalDuration / (1000 * 60 * 60)) < 73;

                    if (update) {
                        if (musicSrv.isPlaying()) {
                            btnPlay.setImageResource(R.drawable.pause);
                            play=true;
                        } else {
                            play=false;
                            btnPlay.setImageResource(R.drawable.play_with_circle);
                        }
                        // Displaying Total Duration time
                        songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                        // Displaying time completed playing
                        songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                        // Updating progress bar
                        int progress = utils.getProgressPercentage(currentDuration, totalDuration);
                        //Log.d("Progress", ""+progress);
                        songProgressBar.setProgress(progress);


                        if (old_ID != musicSrv.getCurrentID() && !songList.isEmpty()) {
                            old_ID = musicSrv.getCurrentID();
                            songTitleLabel.setText(songList.get(currentSongIndex).getTitle());
                            songArtist.setText(Constant.BY + songList.get(currentSongIndex).getArtist());
                            changeBackground();
                        }
                        album_art.setClickable(true);
                    }
                }

                // Running this thread after 500 milliseconds
                mHandler.postDelayed(this, 500);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            //check is song is already playing from song menu

            if (musicSrv != null) {

                if (Constant.comeFromSongMenu) {
                    musicSrv.createNotification();
                    Constant.currentPlaylist = musicSrv.getSongList();
                    songList = musicSrv.getSongList();
                    createSongList();
                    saveLastPlayList();
                    currentSongIndex = musicSrv.getSongPosn();
                    //long totalDuration = musicSrv.getDuration();
                    //long currentDuration = musicSrv.getCurrentPosition();

                    if (!songList.isEmpty()) {
                        songTitleLabel.setText(songList.get(currentSongIndex).getTitle());
                        songArtist.setText("by " + songList.get(currentSongIndex).getArtist());
                    }
                    // Displaying Total Duration time
                    //songTotalDurationLabel.setText("" + utils.milliSecondsToTimer(totalDuration));
                    // Displaying time completed playing
                    //songCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));

                    // Updating progress bar
                    //int progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                    //Log.d("Progress", ""+progress);
                    //songProgressBar.setProgress(progress);

                    updateProgressBar();

                } else {
                    musicSrv.setList(Constant.currentPlaylist);
                    saveLastPlayList();
                    currentSongIndex = Constant.songPosition;
                    songList = musicSrv.getSongList();
                    createSongList();
                    playMusic();
                    musicSrv.createNotification();
                }
            }
            //Toast.makeText(Play.this,"Service connected",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private boolean isShuffle = false;
    //dialog initialization
    private Dialog addToPlaylist, savePLaylist, shareDialog;
    private TextView savePlaylistNameAs, savePlaylistName, addToPlaylistHeading, playListCancel, playListDone;
    private EditText etPlayListName;
    //playlist item
    private List<Artist_Genrs_Album> playlistItem;

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                String data = cursor.getString(column_index);
                cursor.close();
                return data;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    public void onSuperCreate() {
        setContentView(R.layout.layout_play_screen);

        checkFromFile();
        album_art = findViewById(R.id.play_album_art);
        album_art.setClickable(false);
        main_view = findViewById(R.id.main_content);
        //changeTempBackground(false);
        metaRetriver = new MediaMetadataRetriever();
        utils = new Utilities();
        isShuffle = PreferenceManager.getInstance(this).getSuffle();
        Constant.reapeatAll = PreferenceManager.getInstance(this).getRepeat();

        playlistItem = new ArrayList<>();

        //last saved value
        isPLayList = this.getIntent().getBooleanExtra(Constant.ISPLAYLIST, false);
        playListName = this.getIntent().getStringExtra(Constant.PLALISTNAME);

        if (isTablet(this)) {
            ofset = getResources().getDimensionPixelSize(R.dimen.menu_size1);
        } else {
            ofset = getResources().getDimensionPixelSize(R.dimen.menu_size);
        }

        playListDialog = new Dialog(this);
        playListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        playListDialog.setContentView(R.layout.layout_play_list);
        playlistCancel = playListDialog.findViewById(R.id.btn_playlist_menu_cancel);
        playlistCancel.setOnClickListener(this);

        addToPlaylist = new Dialog(this);
        addToPlaylist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addToPlaylist.setContentView(R.layout.add_to_playlist);

        savePLaylist = new Dialog(this);
        savePLaylist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        savePLaylist.setContentView(R.layout.save_play_list);

        //save playlist control with language seeting
        savePlaylistNameAs = savePLaylist.findViewById(R.id.save_playlis_as_heading);

        savePlaylistName = savePLaylist.findViewById(R.id.save_playlist_name);

        //add to playlist for language
        addToPlaylistHeading = addToPlaylist.findViewById(R.id.add_to_playList_heading);

        //playlist menu

        etPlayListName = savePLaylist.findViewById(R.id.et_play_list_name);
        playListCancel = savePLaylist.findViewById(R.id.btn_playlist_cancel);
        playListDone = savePLaylist.findViewById(R.id.btn_playlist_done);


        playListCancel.setOnClickListener(this);
        playListDone.setOnClickListener(this);


        //get playlist name
        getPlaylistItem();

        plaListView = addToPlaylist.findViewById(R.id.play_list_view);
        playListMenuAdapter = new ArtistGenrsAlbumAdapter(this, playlistItem);
        playListMenuAdapter.setClickListhener(new ArtistGenrsAlbumAdapter.ClickListhener() {

            @Override
            public void ItemClick(View view, int position) {
                if (position == 0) {
                    //playListMenu.setVisibility(View.GONE);
                    addToPlaylist.dismiss();
                    savePLaylist.show();
                } else {

                    List<Song> tempPlaylistValue = PreferenceManager.getInstance(getApplicationContext()).getPlayListFirstPosition(position);

                    if (tempPlaylistValue == null) {
                        tempPlaylistValue = new ArrayList<Song>();
                    }

                    tempPlaylistValue.add(musicSrv.getSongList().get(musicSrv.getSongPosn()));


                    //check same song duplication

                    for (int j = 0; j < tempPlaylistValue.size(); j++) {
                        for (int k = j + 1; k < tempPlaylistValue.size(); k++) {
                            if (tempPlaylistValue.get(j).getId() == (tempPlaylistValue.get(k).getId())) {
                                tempPlaylistValue.remove(k);
                                k--;
                            }
                        }
                    }

                    //save to playlist
                    PreferenceManager.getInstance(getApplicationContext()).setPlayListFirstPosition(position, tempPlaylistValue);
                    Toast.makeText(PlayActivity.this, getApplicationContext().getResources().getString(addedToPlaylist), Toast.LENGTH_SHORT).show();
                    addToPlaylist.dismiss();

                }
            }
        });

        plaListView.setAdapter(playListMenuAdapter);
        plaListView.setLayoutManager(new LinearLayoutManager(this));

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        shareDialog = new Dialog(this);
        shareDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        shareDialog.setContentView(R.layout.sharelayout);

        shareContent = shareDialog.findViewById(R.id.share_content);
        shareImage = shareDialog.findViewById(R.id.share_image);
        shareText = shareDialog.findViewById(R.id.share_edit_text);
        btnShareShare = shareDialog.findViewById(R.id.btn_share_share);
        btnShareCancel = shareDialog.findViewById(R.id.btn_share_cancel);
        btnShareCancel.setOnClickListener(this);
        btnShareShare.setOnClickListener(this);
        shareImage.setOnClickListener(this);

        //// TODO: 7/10/2016  change share button language here


        btnPlay = findViewById(R.id.play_play);
        btnNext = findViewById(R.id.play_next);
        btnPrevious = findViewById(R.id.play_previous);
        btnAddToPlaylist = findViewById(R.id.play_btn_add_to_playlist);

        songTitleLabel = findViewById(R.id.play_song_name);
        songTitleLabel.setSelected(true);
        songArtist = findViewById(R.id.play_artist);

        songCurrentDurationLabel = findViewById(R.id.play_elapsed_time);
        songTotalDurationLabel = findViewById(R.id.play_total_time);
        songProgressBar = findViewById(R.id.play_progress);

        songProgressBar.setOnSeekBarChangeListener(this);

        btnPlaylist = findViewById(R.id.play_playlist);
        btnSuffle = findViewById(R.id.play_suffle);
        btnReapeat = findViewById(R.id.play_repeat);
        btnPlayEqualizer = findViewById(R.id.play_equalizer);

        //set click listener
        btnPlaylist.setOnClickListener(this);
        btnReapeat.setOnClickListener(this);
        btnSuffle.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnAddToPlaylist.setOnClickListener(this);
        btnPlayEqualizer.setOnClickListener(this);
    }

    private void checkFromFile() {

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri returnUri = getIntent().getData();
            File file = new File(getPath(this, returnUri));
            Constant.currentPlaylist = new ArrayList<Song>();


            //projection for the info
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.DATE_ADDED,
                    MediaStore.Audio.Media.DATA

            };

            Cursor cursor = this.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Media.DATA + " like ? ",
                    new String[]{"%" + file.getParent() + "%"},
                    null);

            //add the song on allsong l
            while (cursor.moveToNext()) {

                String genr = "<unknown>";

                try {
                    Constant.currentPlaylist.add(new Song(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                            cursor.getString(2), cursor.getString(3), genr, cursor.getString(4), cursor.getString(5), cursor.getString(6)));

                    if (cursor.getString(6).contains(file.getPath())) {
                        Constant.songPosition = Constant.currentPlaylist.size() - 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
            Constant.totalSong = Constant.currentPlaylist.size() + "";
            playFromFile = true;
        }
    }

    private void changeBackground() {
        try {
            metaRetriver.setDataSource(PlayActivity.this, musicSrv.trackUri);
            try {
                art = metaRetriver.getEmbeddedPicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (art != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                Drawable ob = new BitmapDrawable(getResources(), songImage);
                if (songImage != null) {
                    album_art.setImageDrawable(ob);
                    ob = new BitmapDrawable(getResources(), blurFilter.fastblur(songImage, 1, 15));
                    main_view.setBackground(ob);

                }
            } else {
                changeTempBackground();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeTempBackground() {
        BitmapDrawable ob1;
        switch (PreferenceManager.getInstance(this).getImageConstant()) {
            case 0:
                album_art.setImageResource(R.drawable.round_image);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image), 1, 15));
                main_view.setBackground(ob1);
                break;
            case 1:
                album_art.setImageResource(R.drawable.round_image1);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image1), 1, 15));
                main_view.setBackground(ob1);
                break;
            case 2:
                album_art.setImageResource(R.drawable.round_image2);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image2), 1, 15));
                main_view.setBackground(ob1);
                break;
            case 3:
                album_art.setImageResource(R.drawable.round_image3);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image3), 1, 15));
                main_view.setBackground(ob1);
                break;
            case 4:
                album_art.setImageResource(R.drawable.round_image4);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image4), 1, 15));
                main_view.setBackground(ob1);
                break;
            case 5:
                album_art.setImageResource(R.drawable.round_image5);
                ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image5), 1, 15));
                main_view.setBackground(ob1);
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();
    }

    private void updateLanguage() {
        addToPlaylistHeading.setText(getResources().getStringArray(R.array.Add_to_Playlist)[Constant.LANGUAGE_POSITION]);
        savePlaylistNameAs.setText(getResources().getStringArray(R.array.Save_Playlist_as)[Constant.LANGUAGE_POSITION]);
        savePlaylistName.setText(getResources().getStringArray(R.array.Name)[Constant.LANGUAGE_POSITION]);
        playListDone.setText(getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        playListCancel.setText(getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);

    }

    //song list create

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
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

    private void createSongList() {
        songView = playListDialog.findViewById(R.id.playlist_view);

        //songView.setNestedScrollingEnabled(false);
        songAdapter = new SongAdapter(this, songList);
        songAdapter.setClickListhener(this);
        songView.setAdapter(songAdapter);

        songView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView

                if (musicSrv != null && Constant.currentPlaylist.size() > 1) {
                    Constant.currentPlaylist.remove(viewHolder.getAdapterPosition());
                    if (isPLayList) {
                        PreferenceManager.getInstance(getApplicationContext()).removePlaylistByname(playListName);
                        PreferenceManager.getInstance(getApplicationContext()).setPlayListByName(playListName, Constant.currentPlaylist);
                        Toast.makeText(PlayActivity.this, getApplicationContext().getResources().getString(R.string.songRemoved), Toast.LENGTH_SHORT).show();
                    }
                    musicSrv.setList(Constant.currentPlaylist);

                    saveLastPlayList();
                    musicSrv.setSong(0);
                    songList = musicSrv.getSongList();
                    createSongList();
                    playMusic();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(songView);

    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 20);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (musicSrv != null) {
            mHandler.removeCallbacks(mUpdateTimeTask);
            long totalDuration = musicSrv.getDuration();
            int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);
            // forward or backward to certain seconds
            musicSrv.seekTo(currentPosition);
            // update timer progress again
            updateProgressBar();
        }
    }

    private void saveLastPlayList() {
        PreferenceManager.getInstance(this).setlastPlayedMenu(Constant.currentPlaylist);
    }

    private void playMusic() {

        if (musicSrv != null && !songList.isEmpty()) {

            // Updating progress bar
            songProgressBar.setMax(100);
            songProgressBar.setProgress(0);


            // Displaying Total Duration time
            songTotalDurationLabel.setText(Constant.SETDURATIONLABEL);//+ utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            songCurrentDurationLabel.setText(Constant.SETDURATIONLABEL);// + utils.milliSecondsToTimer(currentDuration));

            musicSrv.setSong(currentSongIndex);
            musicSrv.playSong(play);
            if (play) {
                btnPlay.setImageResource(R.drawable.pause);
            } else {
                btnPlay.setImageResource(R.drawable.play_with_circle);
            }

            songTitleLabel.setText(songList.get(currentSongIndex).getTitle());
            songArtist.setText(Constant.BY + songList.get(currentSongIndex).getArtist());


            updateProgressBar();
        }
    }

    private void playNextSong() {


        if (musicSrv != null && isShuffle) {
            // shuffle is on - play a random song
            Random rand = new Random();
            currentSongIndex = rand.nextInt((musicSrv.getSongList().size() - 1) - 0 + 1) + 0;
        } else {
            // no repeat or shuffle ON - play next song
            if (!songList.isEmpty()) {
                if (currentSongIndex < (songList.size() - 1)) {
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play first song
                    currentSongIndex = 0;
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //button play
            case R.id.play_play:
                if (musicSrv != null) {
                    musicSrv.createNotification();
                    if (play) {
                        btnPlay.setImageResource(R.drawable.play_with_circle);
                        if (musicSrv.isPlaying()) {
                            Constant.nothingSelected = false;
                            musicSrv.pauseSong();
                        }
                        play = false;
                    } else {
                        btnPlay.setImageResource(R.drawable.pause);
                        play = true;

                        if (!Constant.nothingSelected) {
                            musicSrv.playCurrentPosition();
                        } else {
                            playMusic();
                        }
                    }
                }
                break;

            //button previous
            case R.id.play_previous:
                if (musicSrv != null) {
                    btnPrevious.setClickable(false);
                    musicSrv.createNotification();
                    if (!songList.isEmpty()) {
                        if (currentSongIndex > 0) {
                            currentSongIndex = currentSongIndex - 1;
                        } else {
                            // play last song
                            currentSongIndex = songList.size() - 1;
                        }
                        playMusic();
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnPrevious.setClickable(true);
                        }
                    }, 200);
                }
                break;
            //button next
            case R.id.play_next:
                if (musicSrv != null) {
                    btnNext.setClickable(false);

                    musicSrv.createNotification();
                    playNextSong();
                    playMusic();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnNext.setClickable(true);
                        }
                    }, 200);
                }
                break;
            //equalizer
            case R.id.play_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;
            //suffle
            case R.id.play_suffle:
                if (musicSrv != null) {
                    if (isShuffle) {
                        isShuffle = false;
                        musicSrv.setShuffle(isShuffle);
                        Toast.makeText(getApplicationContext(), Constant.SUFFLE_OFF, Toast.LENGTH_SHORT).show();
                        //btnShuffle.setImageResource(R.drawable.btn_shuffle);
                    } else {
                        // make repeat to true
                        isShuffle = true;
                        Toast.makeText(getApplicationContext(), Constant.SUFFLE_ON, Toast.LENGTH_SHORT).show();
                        // make shuffle to false

                        musicSrv.setShuffle(isShuffle);
                        Constant.reapeatAll = 0;

                    }
                    PreferenceManager.getInstance(this).setSuffle(isShuffle);
                    PreferenceManager.getInstance(this).setRepeat(Constant.reapeatAll);
                }
                break;
            //repeat
            case R.id.play_repeat:
                if (musicSrv != null) {
                    Constant.reapeatAll = PreferenceManager.getInstance(this).getRepeat();
                    if (Constant.reapeatAll == 0) {
                        Toast.makeText(getApplicationContext(), Constant.REPEAT_ONE, Toast.LENGTH_SHORT).show();
                        // make shuffle to false
                        isShuffle = false;
                        // musicSrv.setRepeat(isRepeat);
                        musicSrv.setShuffle(isShuffle);
                        Constant.reapeatAll = 1;
                    } else if (Constant.reapeatAll == 1) {
                        Constant.reapeatAll = 2;
                        Toast.makeText(getApplicationContext(), Constant.REPEAT_ALL, Toast.LENGTH_SHORT).show();
                        // make shuffle to false
                        isShuffle = false;
                        // musicSrv.setRepeat(isRepeat);
                        musicSrv.setShuffle(isShuffle);
                    } else {
                        // make repeat to true
                        Constant.reapeatAll = 0;
                        Toast.makeText(getApplicationContext(), Constant.REPEAT_OFF, Toast.LENGTH_SHORT).show();
                    }

                    PreferenceManager.getInstance(this).setRepeat(Constant.reapeatAll);
                    PreferenceManager.getInstance(this).setSuffle(isShuffle);

                }
                break;

            //playlist
            case R.id.play_playlist:
                playListDialog.show();
                break;
            //share
            case R.id.play_album_art:
                if (musicSrv != null) {
                    shareDialog.show();
                    try {
                        metaRetriver.setDataSource(PlayActivity.this, musicSrv.trackUri);
                        art = metaRetriver.getEmbeddedPicture();
                        if (art != null) {
                            Bitmap shareImageRow = BitmapFactory.decodeByteArray(art, 0, art.length);
                            Drawable ob3 = new BitmapDrawable(getResources(), shareImageRow);
                            if (shareImageRow != null) {
                                shareImage.setImageDrawable(ob3);
                            }
                        } else {
                            shareImage.setImageResource(R.drawable.round_image);
                        }
                    } catch (Exception e) {
                    }

                    String text = Constant.LISTHEN_TO_TEXT + Constant.currentPlaylist.get(musicSrv.getSongPosn()).getTitle() + "</b> " + Constant.BY + "<b>" + Constant.currentPlaylist.get(musicSrv.getSongPosn()).getArtist() + Constant.PLAYER_NAME;
                    shareText.setText(Html.fromHtml(text));
                }
                break;

            case R.id.btn_share_cancel:
                shareDialog.dismiss();
                break;
            case R.id.btn_share_share:
                shareText.setCursorVisible(false);
                Bitmap b = getBitmapFromView(shareContent);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, Constant.MUSIC_APP_Tittle);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
                OutputStream outstream;
                try {
                    outstream = getContentResolver().openOutputStream(uri);
                    b.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                    outstream.close();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(share, Constant.SHAREIMAGE_TITTLE));
                shareText.setCursorVisible(true);
                shareDialog.dismiss();
                break;

            case R.id.share_image:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;


            case R.id.btn_playlist_done:

                if (musicSrv != null) {
                    if (etPlayListName.getText() != null) {
                        Constant.name_playlist.add(new Artist_Genrs_Album(etPlayListName.getText().toString()));
                    }

                    PreferenceManager.getInstance(getApplicationContext()).setPlaylistMenu(Constant.name_playlist);

                    List<Song> playlist = new ArrayList<Song>();
                    playlist.add(musicSrv.getSongList().get(musicSrv.getSongPosn()));

                    PreferenceManager.getInstance(getApplicationContext()).setPlayListByName(etPlayListName.getText().toString(), playlist);

                    getPlaylistItem();
                    savePLaylist.dismiss();
                }
                break;
            case R.id.btn_playlist_cancel:
                savePLaylist.dismiss();
                break;

            case R.id.play_btn_add_to_playlist:
                addToPlaylist.show();
                break;

            case R.id.btn_playlist_menu_cancel:
                if (playListDialog != null && playListDialog.isShowing()) {
                    playListDialog.dismiss();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            shareImage.setImageBitmap(getScaledBitmap(picturePath, 800, 800));
        }

    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    @Override
    public void ItemClick(View view, int position) {
        currentSongIndex = position;
        play = true;
        playMusic();
        if (playListDialog.isShowing()) {
            playListDialog.dismiss();
        }
    }

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

        try {
            if (musicSrv != null && musicSrv.isPlaying()) {
                Constant.musicPlaying = true;
                musicSrv.createNotification();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //for get playlist item

    //for handling music server
    @Override
    public void onBackPressed() {
        if (musicSrv != null) {
            if (musicSrv.isPlaying()) {
                Constant.musicPlaying = true;
                musicSrv.createNotification();

            } else {
                mHandler.removeCallbacks(mUpdateTimeTask);
                musicSrv.closeNotification();
                musicSrv.stopForeground(true);
                //unbindService(musicConnection);
                //stopService(playPlayIntent);
                musicSrv = null;
                Constant.musicPlaying = false;
                //startActivity(new Intent(this,MainActivity.class));
            }
        }
        super.onBackPressed();
    }

    private void getPlaylistItem() {

        Constant.name_playlist = PreferenceManager.getInstance(getApplicationContext()).getPlaylistMenu();


        if (Constant.name_playlist == null) {
            Constant.name_playlist = new ArrayList<Artist_Genrs_Album>();
        }

        playlistItem.add(new Artist_Genrs_Album(Constant.CREAT_NEW_TEXT));

        if (Constant.name_playlist.size() > 0) {
            for (int i = 0; i < Constant.name_playlist.size(); i++) {
                playlistItem.add(Constant.name_playlist.get(i));
            }
        }
    }

    @Override
    public void onSuperDestroy() {

    }
}
