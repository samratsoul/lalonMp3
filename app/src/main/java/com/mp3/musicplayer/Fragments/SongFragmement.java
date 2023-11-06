package com.mp3.musicplayer.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.mp3.musicplayer.Activity.EqualizerActivity;
import com.mp3.musicplayer.Activity.PlayActivity;
import com.mp3.musicplayer.Activity.SettingActivity;
import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
import com.mp3.musicplayer.Adapters.SongAdapter;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Multiple_Selection;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Services.MusicService;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;
import com.mp3.musicplayer.Utils.Utilities;

import org.cmc.music.common.ID3WriteException;
import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


public class SongFragmement extends Fragment implements View.OnClickListener, SongAdapter.ClickListhener, SongAdapter.LongClickListhener {

    private static final int RESULT_LOAD_IMAGE = 666;
    private static final int PERMISSION_REQUEST_CODE = 420;
    private final List<Song> selectedSong = new ArrayList<>();
    ArtistGenrsAlbumAdapter playListMenuAdapter;
    Animation animFadein;
    private VerticalRecyclerViewFastScroller fastScroller;
    //music service connection for music play
    private MusicService musicSrv;
    private Intent playIntent;
    private CircleImageView shareImage;
    private EditText shareText;
    private boolean serviceBind=false;
    private boolean initializedAd=false;
    //for album art
    private MediaMetadataRetriever metaRetriver;
    //playlist item
    private List<Artist_Genrs_Album> playlistItem;
    private byte[] art;
    private Context context;
    private boolean longpress_clicked = false;
    private Handler menuClick, songUpdate;
    private TextView addToPlaylistHeading, savePlaylistName, savePlaylistNameAs;
    private TextView btnShareShare, btnShareCancel;
    private Dialog deleteDialog, addToPlaylist, savePLaylist, editTagDialog, sortTypeDialog, shareDialog;
    private TextView editTagHeading, editTagTittle, editTagArtist, editTagAlbum;
    private List<Multiple_Selection> multiple_selections;
    private long currSongPosition = 0;
    private EditText etPlayListName;
    private TextView playListDone, playListCancel;
    private TextView tvTotalSong;
    private ImageButton equalizer, play, setting, search, record;
    private OnFragmentInteractionListener mListener;
    private List<Song> songs;
    private final ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicSrv = binder.getService();
            if (musicSrv != null) {
                musicSrv.setList(songs);
                musicSrv.setSong(0);
            }
            // musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //musicBound = false;
        }
    };
    private boolean showingAdd = false, songPause = false;
    private boolean isMainActivityRunning = false;
    private InterstitialAd mInterstitialAds;
    private RecyclerView songView, plaListView;
    private ImageButton appShare, sort_type;
    private SongAdapter songAdapter;
    private LinearLayout sortHolder, normal_landing, search_bar, long_press, delete_menu,
            editTagMenu, longMultipleMenu, songsPlay, playListMenu, createNewPlaylistMenu, shareContent;
    private RelativeLayout llPlaylist;
    private final Runnable mHideScrollBar = new Runnable() {
        public void run() {
            llPlaylist.setBackgroundResource(R.color.null_color);
            fastScroller.setVisibility(View.GONE);
        }
    };
    private ImageButton btnPlaylist, btnEdit, btnRington, btnDelete, btnShare, btnLongPressClose;
    private ImageButton longMultiplePlaylist, longMultiplePlay, longMultipleDelete, longMultipleClose;
    private RadioButton sortByName, sortByArtist, sortByAlbum, sortByDateAdded, sortByDuration, sortByAscending, sortByDecending;
    private TextView sortHeading, sortDone, sortCancel;
    private TextView tvSongPlayName, tvSongArtist;
    private ImageButton imSongPlayPause;
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                if (musicSrv != null && musicSrv.isPlaying() && !Constant.currentPlaylist.isEmpty()) {
                    tvTotalSong.setVisibility(View.GONE);
                    songsPlay.setVisibility(View.VISIBLE);
                    tvSongArtist.setText(Constant.currentPlaylist.get(musicSrv.getSongPosn()).getArtist());
                    tvSongPlayName.setText(Constant.currentPlaylist.get(musicSrv.getSongPosn()).getTitle());
                    imSongPlayPause.setImageResource(R.drawable.song_play_pause);

                } else {
                    imSongPlayPause.setImageResource(R.drawable.song_play);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            songUpdate.postDelayed(this, 500);
        }
    };
    private TextView btnDeleteCancel, btnDeleteOK;
    private TextView btnEditCancel, btnEditOK;
    private EditText etTittle, etArtist, etAlbum;
    private ImageButton btnSearchText, btnSearchClose;
    private EditText etSearchText;
    private MediaRecorder recorder;
    private Button btnCancel;
    private ImageButton imBtnRecord;
    private EditText etFileName;
    private RadioGroup rgBitrate;
    private TextView tvRecordingTimeShow;
    private Dialog recordingDialog;
    private int bitrate = 128;
    private String currentfileName = "";
    private boolean recording = false;
    private long currentTime = 0;
    private Handler durationHandler, playHandler;
    private Utilities utilities;
    private long time = 0;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time++;
            tvRecordingTimeShow.setText(utilities.milliSecondsToTimer(1000 * time));
            durationHandler.postDelayed(this, 1000);
        }
    };
    private String currSongPath = "";

    public SongFragmement() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (PreferenceManager.getInstance(getActivity()).canShowADS()) {
            initializeInterstitialAds();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(com.mp3.musicplayer.R.layout.fragment_song_fragmement, container, false);

        final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        alphaAnimation.setDuration(1000);

        utilities = new Utilities();


        //two handler for menu balck shade and song name and artist update on play or pause
        songUpdate = new Handler();
        menuClick = new Handler();

        llPlaylist = v.findViewById(R.id.ll_playlist);

        songs = new ArrayList<Song>();

        //get current song form all song

        getCurrentSong();

        tvTotalSong = v.findViewById(R.id.tv_total_song_view);
        //total song show
        if (songs.size() != 0) {
            tvTotalSong.setText(songs.size() + " " + getActivity().getResources().getStringArray(R.array.songs)[Constant.LANGUAGE_POSITION]);
        }
        songView = v.findViewById(R.id.song_view);
        fastScroller = v.findViewById(R.id.fast_scroller);
        fastScroller.setRecyclerView(songView);
        songView.setOnScrollListener(fastScroller.getOnScrollListener());

        //sort song
        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        showSong();

        //all the recordingDialog are initial here

        deleteDialog = new Dialog(getActivity());
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.delete_confirm);

        addToPlaylist = new Dialog(getActivity());
        addToPlaylist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addToPlaylist.setContentView(R.layout.add_to_playlist);

        savePLaylist = new Dialog(getActivity());
        savePLaylist.requestWindowFeature(Window.FEATURE_NO_TITLE);
        savePLaylist.setContentView(R.layout.save_play_list);


        editTagDialog = new Dialog(getActivity());
        editTagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editTagDialog.setContentView(R.layout.edit_tag);

        sortTypeDialog = new Dialog(getActivity());
        sortTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sortTypeDialog.setContentView(R.layout.sort_type);

        //initial edit tag

        editTagHeading = editTagDialog.findViewById(R.id.edittag_heading);

        editTagTittle = editTagDialog.findViewById(R.id.edit_tag_tittle);
        editTagAlbum = editTagDialog.findViewById(R.id.edit_tag_album);
        editTagArtist = editTagDialog.findViewById(R.id.edit_tag_artist);


        //save playlist control with language seeting
        savePlaylistNameAs = savePLaylist.findViewById(R.id.save_playlis_as_heading);

        savePlaylistName = savePLaylist.findViewById(R.id.save_playlist_name);


        //add to playlist for language
        addToPlaylistHeading = addToPlaylist.findViewById(R.id.add_to_playList_heading);

        metaRetriver = new MediaMetadataRetriever();
        animFadein = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        playlistItem = new ArrayList<>();
        multiple_selections = new ArrayList<>();


//button
        equalizer = v.findViewById(R.id.btn_equalizer);
        play = v.findViewById(R.id.btn_play);
        appShare = v.findViewById(R.id.btn_app_share);
        sort_type = v.findViewById(R.id.btn_sort_type);
        setting = v.findViewById(R.id.btn_setting);
        search = v.findViewById(R.id.btn_search);
        record = v.findViewById(R.id.btn_record);


        search.setOnClickListener(this);
        setting.setOnClickListener(this);
        sort_type.setOnClickListener(this);
        appShare.setOnClickListener(this);
        equalizer.setOnClickListener(this);
        play.setOnClickListener(this);
        record.setOnClickListener(this);


        normal_landing = v.findViewById(R.id.ll_normal_landing);

        // normal_landing.setAnimation(animFadein);
        search_bar = v.findViewById(R.id.ll_search_bar);
        //menuContainer = (LinearLayout) v.findViewById(R.id.menu_container);
        long_press = v.findViewById(R.id.ll_long_single_press);

        //delete recordingDialog bajckground set here
        delete_menu = deleteDialog.findViewById(R.id.ll_delete_menu);


        editTagMenu = editTagDialog.findViewById(R.id.ll_edit_tag);
        longMultipleMenu = v.findViewById(R.id.ll_long_multiple_menu);
        songsPlay = v.findViewById(R.id.songs_play_menu);
        playListMenu = addToPlaylist.findViewById(R.id.play_list_menu);
        createNewPlaylistMenu = savePLaylist.findViewById(R.id.create_new_playlist);


        songsPlay.setOnClickListener(this);

        //play container

        tvSongPlayName = v.findViewById(R.id.songs_name);
        tvSongArtist = v.findViewById(R.id.songs_artist);
        imSongPlayPause = v.findViewById(R.id.songs_play);
        imSongPlayPause.setOnClickListener(this);

        //sort container

        sortHolder = sortTypeDialog.findViewById(R.id.sort_holder);

        sortHeading = sortTypeDialog.findViewById(R.id.tv_sort_heading);
        sortByName = sortTypeDialog.findViewById(R.id.sort_by_name);
        sortByArtist = sortTypeDialog.findViewById(R.id.sort_by_artist);
        sortByAlbum = sortTypeDialog.findViewById(R.id.sort_by_album);
        sortByDuration = sortTypeDialog.findViewById(R.id.sort_by_duration);
        sortByDateAdded = sortTypeDialog.findViewById(R.id.sort_by_date_added);
        sortByAscending = sortTypeDialog.findViewById(R.id.sort_by_assending);
        sortByDecending = sortTypeDialog.findViewById(R.id.sort_by_descending);
        sortDone = sortTypeDialog.findViewById(R.id.btn_sort_done);
        sortCancel = sortTypeDialog.findViewById(R.id.btn_sort_cancel);
        sortDone.setOnClickListener(this);
        sortCancel.setOnClickListener(this);

        //share content
        shareDialog = new Dialog(getActivity());
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
        playHandler = new Handler();


        btnSearchText = v.findViewById(R.id.btn_search_text);
        btnSearchClose = v.findViewById(R.id.btn_search_close);

        etSearchText = v.findViewById(R.id.et_search_text);

        etSearchText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // Perform action on key press
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
                        return true;
                    }
                }
                return false;
            }
        });
        // etSearchText.setOnClickListener(this);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search();
            }
        });
        btnSearchText.setOnClickListener(this);
        btnSearchClose.setOnClickListener(this);


        // long press song

        btnPlaylist = v.findViewById(R.id.btn_playlist);
        btnEdit = v.findViewById(R.id.btn_edit);
        btnRington = v.findViewById(R.id.btn_ringtone);
        btnDelete = v.findViewById(R.id.btn_delete);
        btnShare = v.findViewById(R.id.btn_share);
        btnLongPressClose = v.findViewById(R.id.btn_long_press_close);


        btnPlaylist.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnRington.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnLongPressClose.setOnClickListener(this);

        //delete menu

        btnDeleteCancel = deleteDialog.findViewById(R.id.btn_delete_cancel);
        btnDeleteOK = deleteDialog.findViewById(R.id.btn_delete_ok);


        btnDeleteCancel.setOnClickListener(this);
        btnDeleteOK.setOnClickListener(this);

        //edit tag menu

        etTittle = editTagDialog.findViewById(R.id.et_edit_tittle);
        etArtist = editTagDialog.findViewById(R.id.et_edit_artist);
        etAlbum = editTagDialog.findViewById(R.id.et_edit_album);

        btnEditCancel = editTagDialog.findViewById(R.id.btn_edit_cancel);
        btnEditOK = editTagDialog.findViewById(R.id.btn_edit_ok);


        btnEditCancel.setOnClickListener(this);
        btnEditOK.setOnClickListener(this);


        // long multiple selection

        longMultiplePlaylist = v.findViewById(R.id.btn_long_multiple_playlist);
        longMultiplePlay = v.findViewById(R.id.btn_long_multiple_play);
        longMultipleDelete = v.findViewById(R.id.btn_long_multiple_delete);
        longMultipleClose = v.findViewById(R.id.btn_long_multiple_close);

        longMultiplePlaylist.setOnClickListener(this);
        longMultiplePlay.setOnClickListener(this);
        longMultipleDelete.setOnClickListener(this);
        longMultipleClose.setOnClickListener(this);

        //playlist menu

        etPlayListName = savePLaylist.findViewById(R.id.et_play_list_name);
        playListCancel = savePLaylist.findViewById(R.id.btn_playlist_cancel);
        playListDone = savePLaylist.findViewById(R.id.btn_playlist_done);

        playListCancel.setOnClickListener(this);
        playListDone.setOnClickListener(this);

        //get playlist name
        getPlaylistItem();

        plaListView = addToPlaylist.findViewById(R.id.play_list_view);
        playListMenuAdapter = new ArtistGenrsAlbumAdapter(getActivity(), playlistItem);
        playListMenuAdapter.setClickListhener(new ArtistGenrsAlbumAdapter.ClickListhener() {

            @Override
            public void ItemClick(View view, int position) {
                if (position == 0) {
                    addToNowPlaying();
                    addToPlaylist.dismiss();
                    //playListMenu.setVisibility(View.GONE);
                    //menuContainer.setVisibility(View.GONE);
                } else if (position == 1) {
                    //playListMenu.setVisibility(View.GONE);
                    addToPlaylist.dismiss();
                    savePLaylist.show();
                } else {

                    List<Song> tempPlaylistValue;
                    tempPlaylistValue = PreferenceManager.getInstance(getActivity()).getPlayListSecondPosition(position);

                    if (tempPlaylistValue == null) {
                        tempPlaylistValue = new ArrayList<Song>();
                    }


                    for (int i = 0; i < multiple_selections.size(); i++) {
                        for (int j = 0; j < songs.size(); j++) {
                            if (multiple_selections.get(i).getId() == songs.get(j).getId()) {
                                tempPlaylistValue.add(songs.get(j));
                            }
                        }
                    }

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
                    PreferenceManager.getInstance(getActivity()).setPlayListSecondPosition(position, tempPlaylistValue);
                    addToPlaylist.dismiss();

                }
            }
        });
        // playListMenuAdapter.setLongClickListhener(this);
        plaListView.setAdapter(playListMenuAdapter);
        plaListView.setLayoutManager(new LinearLayoutManager(getActivity()));


        normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));

        //handler for song update
        songUpdate.postDelayed(mUpdateTimeTask, 500);

        if (checkPermission()) {
            initialRecoding();
        }
        return v;
    }

    private void initialRecoding() {
        recordingDialog = new Dialog(getActivity());
        recordingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        recordingDialog.setContentView(R.layout.recording_dialog);
        recordingDialog.setCancelable(false);
        btnCancel = recordingDialog.findViewById(R.id.btnCancel);
        imBtnRecord = recordingDialog.findViewById(R.id.imbtnRecording);
        etFileName = recordingDialog.findViewById(R.id.etFileName);
        rgBitrate = recordingDialog.findViewById(R.id.rgBitrateGroup);
        tvRecordingTimeShow = recordingDialog.findViewById(R.id.tvRecordingTime);
        durationHandler = new Handler();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = false;
                stopRecording();
                if (recordingDialog != null) {
                    recordingDialog.dismiss();
                }
            }
        });
        imBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    setCurrentfileName();
                    startRecording();
                    imBtnRecord.setImageResource(R.drawable.stop_recording);
                    recording = true;
                } else {
                    recording = false;
                    stopRecording();
                    imBtnRecord.setImageResource(R.drawable.start_recording);

                }
            }


        });

        rgBitrate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbBitrate1:
                        bitrate = 128;
                        break;

                    case R.id.rbBitrate2:
                        bitrate = 192;
                        break;

                    case R.id.rbBitrate3:
                        bitrate = 256;
                        break;

                    default:
                        bitrate = 128;
                        break;
                }
            }
        });

    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + currentfileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(bitrate);
            recorder.prepare();
            //recorder.setMaxDuration(maxDuration);
            recorder.start();
            time = 0;
            tvRecordingTimeShow.setText(utilities.milliSecondsToTimer(0));
            durationHandler.postDelayed(runnable, 1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + currentfileName))));

            //    getActivity().getContentResolver().notify();


//            addSong(getContext(), Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + currentfileName);


        }
        if (durationHandler != null) {
            durationHandler.removeCallbacks(runnable);
        }
    }

    private void setCurrentfileName() {
        try {
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mp3_Player");
            if (dir.mkdir()) {
                //Toast.makeText(this,"Created",Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this,"Not Created",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        //todo Set Name formate with time
        //Date date = new Date(System.currentTimeMillis());
        //Log.e("TIME_STAMP",date.getMonth()+"_"+date.getYear()+"_"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds());

        currentTime = System.currentTimeMillis();
        currentfileName = "/Mp3_Player/" + etFileName.getText().toString().trim() + currentTime + ".mp3";
    }

    private boolean checkPermission() {

        int result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);


        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void getPlaylistItem() {

        Constant.name_playlist = PreferenceManager.getInstance(getActivity()).getPlaylistMenu();

        if (Constant.name_playlist == null) {
            Constant.name_playlist = new ArrayList<Artist_Genrs_Album>();
        }

        //firts two are define rest are from saved
        playlistItem.add(new Artist_Genrs_Album(Constant.ALBUM_NOW_PLAYING));
        playlistItem.add(new Artist_Genrs_Album(Constant.ALBUM_CREATE_NEW));

        if (Constant.name_playlist.size() > 0) {
            for (int i = 0; i < Constant.name_playlist.size(); i++) {
                playlistItem.add(Constant.name_playlist.get(i));
            }
        }


    }

    //function for add to now
    private void addToNowPlaying() {
        //Constant.currentPlaylist= new ArrayList<Song>();

        for (int i = 0; i < multiple_selections.size(); i++) {
            for (int j = 0; j < songs.size(); j++) {
                if (multiple_selections.get(i).getId() == songs.get(j).getId()) {
                    Constant.currentPlaylist.add(songs.get(j));
                }
            }
        }
        if (Constant.currentPlaylist != null) {
            showInterstitialAd();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onStart() {

        //attach service
        super.onStart();

        if (playIntent == null) {
            playIntent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            serviceBind=true;
            getActivity().startService(playIntent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isMainActivityRunning = true;
        updateLanguage();
        try {
            if (musicSrv != null && musicSrv.isPlaying()) {
                tvTotalSong.setVisibility(View.GONE);
                songsPlay.setVisibility(View.VISIBLE);
                tvSongArtist.setText(songs.get(musicSrv.getSongPosn()).getArtist());
                tvSongPlayName.setText(songs.get(musicSrv.getSongPosn()).getTitle());
                imSongPlayPause.setImageResource(R.drawable.song_play_pause);

            } else {
                imSongPlayPause.setImageResource(R.drawable.song_play);
            }
        } catch (Exception e) {

        }
    }

    private void updateLanguage() {
        //language
        if (songs.size() != 0) {
            tvTotalSong.setText(songs.size() + " " + getActivity().getResources().getStringArray(R.array.songs)[Constant.LANGUAGE_POSITION]);
        }
        editTagHeading.setText(getActivity().getResources().getStringArray(R.array.Edit_Tags)[Constant.LANGUAGE_POSITION]);
        editTagTittle.setText(getActivity().getResources().getStringArray(R.array.Title)[Constant.LANGUAGE_POSITION]);
        editTagAlbum.setText(getActivity().getResources().getStringArray(R.array.Albums)[Constant.LANGUAGE_POSITION]);
        editTagArtist.setText(getActivity().getResources().getStringArray(R.array.Artists)[Constant.LANGUAGE_POSITION]);
        savePlaylistName.setText(getActivity().getResources().getStringArray(R.array.Name)[Constant.LANGUAGE_POSITION]);
        addToPlaylistHeading.setText(getActivity().getResources().getStringArray(R.array.Add_to_Playlist)[Constant.LANGUAGE_POSITION]);
        savePlaylistNameAs.setText(getActivity().getResources().getStringArray(R.array.Save_Playlist_as)[Constant.LANGUAGE_POSITION]);
        btnDeleteOK.setText(getActivity().getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        btnDeleteCancel.setText(getActivity().getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);
        btnEditCancel.setText(getActivity().getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);
        btnEditOK.setText(getActivity().getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        playListDone.setText(getActivity().getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        playListCancel.setText(getActivity().getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);
        sortHeading.setText(getActivity().getResources().getStringArray(R.array.Sort)[Constant.LANGUAGE_POSITION]);
        sortByName.setText(getActivity().getResources().getStringArray(R.array.Title)[Constant.LANGUAGE_POSITION]);
        sortByArtist.setText(getActivity().getResources().getStringArray(R.array.Artists)[Constant.LANGUAGE_POSITION]);
        sortByAlbum.setText(getActivity().getResources().getStringArray(R.array.Albums)[Constant.LANGUAGE_POSITION]);
        sortByDuration.setText(getActivity().getResources().getStringArray(R.array.Duration)[Constant.LANGUAGE_POSITION]);
        sortByDateAdded.setText(getActivity().getResources().getStringArray(R.array.Date_added)[Constant.LANGUAGE_POSITION]);
        sortByAscending.setText(getActivity().getResources().getStringArray(R.array.Short_in_Ascending_Order)[Constant.LANGUAGE_POSITION]);
        sortByDecending.setText(getActivity().getResources().getStringArray(R.array.Short_in_descending_Order)[Constant.LANGUAGE_POSITION]);
        sortDone.setText(getActivity().getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        sortCancel.setText(getActivity().getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_record:
                if (checkPermission()) {
                    if (recordingDialog != null) {
                        tvRecordingTimeShow.setText(utilities.milliSecondsToTimer(0));
                        recordingDialog.show();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
                }
                break;
            case R.id.btn_equalizer:
                startActivity(new Intent(getActivity(), EqualizerActivity.class));
                break;
            //play all song button
            case R.id.btn_play:
                if (songs.size() != 0) {
                    Constant.currentPlaylist = new ArrayList<Song>();
                    Constant.currentPlaylist = songs;
                    showInterstitialAd();
                }
                break;

            //setting button
            case R.id.btn_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;

            //search button
            case R.id.btn_search:

                normal_landing.setVisibility(View.GONE);
                search_bar.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translet_right));
                search_bar.setVisibility(View.VISIBLE);
                etSearchText.requestFocus();
                InputMethodManager imm3 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm3.showSoftInput(etSearchText, InputMethodManager.SHOW_IMPLICIT);

                break;

            //share button
            case R.id.btn_app_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constant.PLAYSTORELINK + getActivity().getPackageName());
                startActivity(Intent.createChooser(sharingIntent, Constant.SHARETITTLE));
                break;

            //sort recordingDialog show
            case R.id.btn_sort_type:
                sortTypeDialog.show();
                break;

//chort cancel and done
            case R.id.btn_sort_cancel:
                sortTypeDialog.dismiss();
                break;
            case R.id.btn_sort_done:
                sortSong();
                break;

            //search edittext
            case R.id.et_search_text:
                // etSearchText.setText("");
                // etSearchText.setHint("");
                break;

            //search close button
            case R.id.btn_search_close:
                search_bar.setVisibility(View.GONE);
                normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
                normal_landing.setVisibility(View.VISIBLE);
                etSearchText.setText("");
                songAdapter.notifyDataSetChanged();
                InputMethodManager imm1 = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);

                break;

            //search function
            case R.id.btn_search_text:
                search();
                break;


            case R.id.btn_playlist:
                addToPlaylist.show();
                break;

            case R.id.btn_edit:
                editSongGet();
                break;

            case R.id.btn_ringtone:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(context)) {
                        Toast.makeText(getActivity(), Constant.RINGTONE_SETTING, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivity(intent);
                        // now wait for onActivityResult()
                    } else {
                        setRingtone();
                    }
                } else {
                    setRingtone();
                }
                break;
            case R.id.btn_delete:
                deleteDialog.show();
                break;
            case R.id.btn_share:
                shareDialog.show();
                try {
                    metaRetriver.setDataSource(getActivity(), musicSrv.trackUri);
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

                String text = "Listening to <b>" + songs.get((int) currSongPosition).getTitle() + "</b> by <b>" + songs.get((int) currSongPosition).getArtist() + "</b> on <b>" + Constant.APP_NAME + "</b>";
                shareText.setText(Html.fromHtml(text));
                break;
            case R.id.btn_long_press_close:
                longpress_clicked = false;
                long_press.setVisibility(View.GONE);
                normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
                normal_landing.setVisibility(View.VISIBLE);
                multiple_selections.clear();
                selectedSong.clear();
                showSong();
                break;


            case R.id.btn_delete_cancel:
                // menuContainer.setVisibility(View.GONE);
                // delete_menu.setVisibility(View.GONE);
                deleteDialog.dismiss();
                break;

            case R.id.btn_delete_ok:

                try {
                    for (int i = 0; i < multiple_selections.size(); i++) {
                        File file = new File(multiple_selections.get(i).getData());
                        file.getAbsoluteFile().delete();
                        getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(multiple_selections.get(i).getData()))));
                    }
                    songs.removeAll(selectedSong);
                    selectedSong.clear();
                    Toast.makeText(getActivity(), Constant.FILE_DELETED_TEXT, Toast.LENGTH_SHORT).show();
                    //  menuContainer.setVisibility(View.GONE);
                    // delete_menu.setVisibility(View.GONE);
                    deleteDialog.dismiss();
                    multiple_selections.clear();
                    selectedSong.clear();
                    longpress_clicked = false;
                    normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
                    normal_landing.setVisibility(View.VISIBLE);
                    showSong();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.btn_edit_cancel:
                editTagDialog.dismiss();
                break;

            case R.id.btn_edit_ok:
                editSongOK();
                songAdapter.notifyDataSetChanged();
                break;


            case R.id.btn_long_multiple_playlist:
                addToPlaylist.show();
                break;


            case R.id.btn_long_multiple_play:
                Constant.currentPlaylist = new ArrayList<Song>();

                for (int i = 0; i < multiple_selections.size(); i++) {
                    for (int j = 0; j < songs.size(); j++) {
                        if (multiple_selections.get(i).getId() == songs.get(j).getId()) {
                            Constant.currentPlaylist.add(songs.get(j));
                        }
                    }
                }
                if (Constant.currentPlaylist != null) {
                    Constant.comeFromSongMenu = false;
                    showInterstitialAd();
                }

                break;

            case R.id.btn_long_multiple_delete:

                deleteDialog.show();
                break;

            case R.id.btn_long_multiple_close:
                longpress_clicked = false;
                multiple_selections.clear();
                selectedSong.clear();
                longMultipleMenu.setVisibility(View.GONE);
                normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translet_right));
                normal_landing.setVisibility(View.VISIBLE);
                showSong();
                break;


            case R.id.songs_play:
                if (musicSrv != null && musicSrv.isPlaying()) {
                    Constant.nothingSelected = false;
                    musicSrv.pauseSong();
                    imSongPlayPause.setImageResource(R.drawable.song_play);

                } else {
                    if (musicSrv != null) {
                        if (!Constant.nothingSelected) {
                            musicSrv.playCurrentPosition();
                        } else {
                            musicSrv.playSong(true);
                        }
                    }
                    imSongPlayPause.setImageResource(R.drawable.song_play_pause);
                }
                break;


            case R.id.btn_playlist_done:


                if (etPlayListName.getText() != null) {
                    Constant.name_playlist.add(new Artist_Genrs_Album(etPlayListName.getText().toString()));
                }

                PreferenceManager.getInstance(getActivity()).setPlaylistMenu(Constant.name_playlist);

                List<Song> playlist = new ArrayList<Song>();
                for (int i = 0; i < multiple_selections.size(); i++) {
                    for (int j = 0; j < songs.size(); j++) {
                        if (multiple_selections.get(i).getId() == songs.get(j).getId()) {
                            playlist.add(songs.get(j));
                        }
                    }
                }
                PreferenceManager.getInstance(getActivity()).setPlayListByName(etPlayListName.getText().toString(), playlist);
                savePLaylist.dismiss();

                break;
            case R.id.btn_playlist_cancel:
                savePLaylist.dismiss();
                break;

            case R.id.songs_play_menu:
                songsPlay.setClickable(false);
                songsPlay.setBackgroundColor(getResources().getColor(R.color.item_click_background));
                menuClick.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        songsPlay.setBackgroundColor(getResources().getColor(R.color.null_color));
                        Constant.comeFromSongMenu = true;
                        showInterstitialAd();
                        songsPlay.setClickable(true);
                    }
                }, 200);

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
                Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
                OutputStream outstream;
                try {
                    outstream = getActivity().getContentResolver().openOutputStream(uri);
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
            default:
                break;
        }
    }

    private void editSongGet() {
        // menuContainer.setVisibility(View.VISIBLE);
        // editTagMenu.setVisibility(View.VISIBLE);

        editTagDialog.show();
        File src = new File(currSongPath);
        MusicMetadataSet src_set = null;
        try {
            src_set = new MyID3().read(src);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // read metadata

        if (src_set == null) // perhaps no metadata
        {
            Log.i("NULL", "NULL");
        } else {
            try {
                IMusicMetadata metadata = src_set.getSimplified();
                String artist = metadata.getArtist();
                String album = metadata.getAlbum();
                //String song_title = metadata.getSongTitle();

                etTittle.setText(songs.get((int) currSongPosition).getTitle());
                etArtist.setText(artist);
                etAlbum.setText(album);

            } catch (Exception e) {
                e.printStackTrace();
                //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void editSongOK() {
        File src = new File(currSongPath);
        MusicMetadataSet src_set = null;
        try {
            src_set = new MyID3().read(src);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } // read metadata

        if (src_set == null) // perhaps no metadata
        {
            Log.i("NULL", "NULL");
        } else {
            MusicMetadata meta;
            if (currSongPath.contains(".mp3")) {
                meta = new MusicMetadata(etTittle.getText().toString() + ".mp3");
            } else {
                meta = new MusicMetadata(etTittle.getText().toString());
            }
            meta.setAlbum(etAlbum.getText().toString());
            meta.setArtist(etArtist.getText().toString());
            meta.setSongTitle(etTittle.getText().toString());

            try {
                new MyID3().update(src, src_set, meta);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ID3WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  // write updated metadata

            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(src)));

            songs.get((int) currSongPosition).setTitle(etTittle.getText().toString());
            songs.get((int) currSongPosition).setAlbum(etAlbum.getText().toString());
            songs.get((int) currSongPosition).setArtist(etArtist.getText().toString());
            songAdapter.notifyDataSetChanged();
        }

        editTagDialog.dismiss();
    }


    private void showSong() {


        songAdapter = new SongAdapter(getActivity(), songs);
        songAdapter.setClickListhener(this);
        songAdapter.setLongClickListhener(this);
        songView.setAdapter(songAdapter);

        songView.setLayoutManager(new LinearLayoutManager(getActivity()));


        songView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (dy == 0) {
                    //llPlaylist.setBackgroundResource(R.color.null_color);
                    //fastScroller.setVisibility(View.GONE);

                } else {
                    fastScroller.setVisibility(View.VISIBLE);
                    //llPlaylist.setBackgroundResource(R.drawable.recycleview_background);
                    menuClick.removeCallbacks(mHideScrollBar);
                    menuClick.postDelayed(mHideScrollBar, 3000);
                }

            }


        });

    }

    private void search() {
        final List<Song> searchSong = new ArrayList<Song>();
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getTitle().toUpperCase().contains(etSearchText.getText().toString().toUpperCase()) || songs.get(i).getArtist().toUpperCase().contains(etSearchText.getText().toString().toUpperCase())) {
                searchSong.add(new Song(songs.get(i).getId(), songs.get(i).getTitle(), songs.get(i).getArtist(), songs.get(i).getAlbum(), songs.get(i).getGeneres(), songs.get(i).getLength(), songs.get(i).getDateAdded(), songs.get(i).getData()));
            }
        }

        songAdapter = new SongAdapter(getActivity(), searchSong);
        songAdapter.setClickListhener(new SongAdapter.ClickListhener() {
            @Override
            public void ItemClick(View view, int position) {
                Constant.currentPlaylist = new ArrayList<Song>();
                Constant.currentPlaylist = searchSong;
                if (musicSrv != null && Constant.currentPlaylist != null) {
                    musicSrv.setList(searchSong);
                    tvSongArtist.setText(searchSong.get(position).getArtist());
                    tvSongPlayName.setText(searchSong.get(position).getTitle());
                    musicSrv.setSong(position);
                    musicSrv.playSong(true);
                    Constant.comeFromSongMenu = true;
                    showInterstitialAd();
                }
            }
        });

        // songAdapter.setLongClickListhener(this);
        songView.setAdapter(songAdapter);
        songView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    private void sortSong() {
        int i = 0, j = 0;
        if (sortByName.isChecked()) i = 1;
        if (sortByArtist.isChecked()) i = 2;
        if (sortByAlbum.isChecked()) i = 3;
        if (sortByDuration.isChecked()) i = 4;
        if (sortByDateAdded.isChecked()) i = 5;

        if (sortByAscending.isChecked()) j = 1;
        if (sortByDecending.isChecked()) j = 2;


        switch (i) {
            case 1:
                if (j == 1) {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getTitle().compareTo(rhs.getTitle());
                        }
                    });
                } else {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getTitle().compareTo(lhs.getTitle());
                        }
                    });
                }

                break;
            case 2:
                if (j == 1) {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getArtist().compareTo(rhs.getArtist());
                        }
                    });
                } else {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getArtist().compareTo(lhs.getArtist());
                        }
                    });
                }
                break;
            case 3:
                if (j == 1) {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getAlbum().compareTo(rhs.getAlbum());
                        }
                    });
                } else {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getAlbum().compareTo(lhs.getAlbum());
                        }
                    });
                }


                break;
            case 4:

                if (j == 1) {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getLength().compareTo(rhs.getLength());
                        }
                    });
                } else {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getLength().compareTo(lhs.getLength());
                        }
                    });
                }


                break;
            case 5:
                if (j == 1) {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getDateAdded().compareTo(rhs.getDateAdded());
                        }
                    });
                } else {
                    Collections.sort(songs, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getDateAdded().compareTo(lhs.getDateAdded());
                        }
                    });
                }
                break;
        }

        sortTypeDialog.dismiss();
        songAdapter.notifyDataSetChanged();

    }

    @Override
    public void ItemClick(final View view, int position) {


        if (longpress_clicked) {
            try {
                if (musicSrv != null && musicSrv.isPlaying()) {
                    musicSrv.pauseSong();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean isSelcted = false;
            if (multiple_selections.size() > 0) {
                for (int i = 0; i < multiple_selections.size(); i++) {
                    if (multiple_selections.get(i).getPosition() == position) {
                        selectedSong.remove(i);
                        multiple_selections.remove(i);
                        isSelcted = true;
                        break;
                    }
                }
            }
            if (isSelcted) {
                view.setBackgroundColor(getActivity().getResources().getColor(R.color.null_color));
            } else {
                view.setBackgroundColor(getActivity().getResources().getColor(R.color.long_select_bakground));
                multiple_selections.add(new Multiple_Selection(position, songs.get(position).getId(), songs.get(position).getData()));
                selectedSong.add(songs.get(position));
            }

            if (multiple_selections.size() > 1) {
                long_press.setVisibility(View.GONE);
                normal_landing.setVisibility(View.GONE);
                long_press.setVisibility(View.GONE);
                longMultipleMenu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
                longMultipleMenu.setVisibility(View.VISIBLE);
            } else if (multiple_selections.size() > 0) {
                currSongPosition = multiple_selections.get(0).getPosition();
                currSongPath = multiple_selections.get(0).getData();
                longMultipleMenu.setVisibility(View.GONE);
                normal_landing.setVisibility(View.GONE);
                long_press.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translet_right));
                long_press.setVisibility(View.VISIBLE);
            }


            if (multiple_selections.size() == 0) {
                longMultipleMenu.setVisibility(View.GONE);
                long_press.setVisibility(View.GONE);
                normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
                normal_landing.setVisibility(View.VISIBLE);
                longpress_clicked = false;
            }
        } else {
            boolean playing = false;
            if (musicSrv != null) {
                playing = musicSrv.isPlaying();
                view.setBackgroundColor(getResources().getColor(R.color.item_click_background));
                menuClick.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(getResources().getColor(R.color.null_color));
                    }
                }, 200);

                tvTotalSong.setVisibility(View.GONE);
                songsPlay.setVisibility(View.VISIBLE);
                musicSrv.setList(songs);
                tvSongArtist.setText(songs.get(position).getArtist());
                tvSongPlayName.setText(songs.get(position).getTitle());
                musicSrv.setSong(position);
                musicSrv.playSong(true);
                imSongPlayPause.setImageResource(R.drawable.song_play_pause);

                //open start page
                Constant.comeFromSongMenu = true;
                if (!playing) {
                    playHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showInterstitialAd();
                        }
                    },200);
                }
            }
        }
    }

    @Override
    public void ItemLongClick(View view, int position) {

        try {
            if (musicSrv != null && musicSrv.isPlaying()) {
                musicSrv.pauseSong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        longpress_clicked = true;
        boolean isSelcted = false;
        if (multiple_selections.size() > 0) {
            for (int i = 0; i < multiple_selections.size(); i++) {
                if (multiple_selections.get(i).getPosition() == position) {
                    selectedSong.remove(i);
                    multiple_selections.remove(i);
                    isSelcted = true;
                    break;
                }
            }
        }
        if (isSelcted) {
            view.setBackgroundColor(getActivity().getResources().getColor(R.color.null_color));
        } else {
            view.setBackgroundColor(getActivity().getResources().getColor(R.color.long_select_bakground));
            multiple_selections.add(new Multiple_Selection(position, songs.get(position).getId(), songs.get(position).getData()));
            selectedSong.add(songs.get(position));
        }

        if (multiple_selections.size() > 1) {
            long_press.setVisibility(View.GONE);
            normal_landing.setVisibility(View.GONE);
            longMultipleMenu.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
            longMultipleMenu.setVisibility(View.VISIBLE);

        } else if (multiple_selections.size() > 0) {
            currSongPosition = position;
            currSongPath = songs.get(position).getData();
            longMultipleMenu.setVisibility(View.GONE);
            normal_landing.setVisibility(View.GONE);
            long_press.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translet_right));
            long_press.setVisibility(View.VISIBLE);
        }


        if (multiple_selections.size() == 0) {
            longMultipleMenu.setVisibility(View.GONE);
            long_press.setVisibility(View.GONE);
            normal_landing.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.translate_left));
            normal_landing.setVisibility(View.VISIBLE);
            longpress_clicked = false;
        }

    }

    private void getCurrentSong() {

        //filter add here
        for (int i = 0; i < Constant.allSongs.size(); i++) {
            songs.add(new Song(Constant.allSongs.get(i).getId(), Constant.allSongs.get(i).getTitle(), Constant.allSongs.get(i).getArtist(), Constant.allSongs.get(i).getAlbum(), Constant.allSongs.get(i).getGeneres(), Constant.allSongs.get(i).getLength(), Constant.allSongs.get(i).getDateAdded(), Constant.allSongs.get(i).getData()));
        }

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
    public void onPause() {
        super.onPause();
        isMainActivityRunning = false;
        try {
            if (musicSrv != null && !musicSrv.isPlaying()) {
                musicSrv.closeNotification();
                musicSrv.stopForeground(true);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {

        if (musicConnection != null && serviceBind) {
            getActivity().unbindService(musicConnection);
            serviceBind=false;
        }
        try {
            if (musicSrv != null && !musicSrv.isPlaying()) {
                musicSrv.stopForeground(true);
                //getActivity().unbindService(musicConnection);
                //getActivity().stopService(playIntent);
                musicSrv = null;
                playIntent = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (recorder != null) {
                recorder.release();
            }

            if (durationHandler != null) {
                durationHandler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialRecoding();
                    tvRecordingTimeShow.setText(utilities.milliSecondsToTimer(0));
                    recordingDialog.show();
                } else {
                    try {
                        Toast.makeText(getActivity(), Constant.PERMISSION_DENY, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

        }
    }

    private void setRingtone() {
        Uri songUri = Uri.parse(currSongPath);
        try {
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE, songUri);
            Toast.makeText(getActivity(), Constant.RINGTONE_SECCESSFULL, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), Constant.RINGTONE_NOTE_SECCESSFULL, Toast.LENGTH_SHORT).show();
        }

    }

    private void initializeInterstitialAds() {
        initializedAd=true;
        mInterstitialAds = new InterstitialAd(getActivity());
        mInterstitialAds.setAdUnitId(getResources().getString(R.string.interstitial_id));
        mInterstitialAds.loadAd(new AdRequest.Builder().build());
        mInterstitialAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //showInterstitial();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                if (showingAdd) {
                    if (musicSrv != null && songPause) {
                        musicSrv.playCurrentPosition();
                    }
                    startActivity(new Intent(getActivity(), PlayActivity.class));
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                //Load interstitial again after showing previous
                //mInterstitialAds.loadAd(new AdRequest.Builder().build());
                if (musicSrv != null && songPause) {
                    musicSrv.playCurrentPosition();
                }
                startActivity(new Intent(getActivity(), PlayActivity.class));
            }
        });
    }

    private void showInterstitialAd() {
        if (PreferenceManager.getInstance(getActivity()).canShowADS() && initializedAd) {

            if (!Utilities.isAppRunning(getActivity(), getActivity().getPackageName()) && !isMainActivityRunning) {
                startActivity(new Intent(getActivity(), PlayActivity.class));
                return;
            }

            if (mInterstitialAds.isLoaded()) {
                if (musicSrv != null && musicSrv.isPlaying()) {
                    musicSrv.pauseSong();
                    songPause = true;
                } else {
                    songPause = false;
                }
                PreferenceManager.getInstance(getActivity()).setNumberOfSongPlayedAfterAds(0);
                PreferenceManager.getInstance(getActivity()).setShowADS(false);
                mInterstitialAds.show();
                showingAdd = true;
            } else {
                showingAdd = false;
                startActivity(new Intent(getActivity(), PlayActivity.class));
            }
        } else {
            startActivity(new Intent(getActivity(), PlayActivity.class));
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

    }
}
