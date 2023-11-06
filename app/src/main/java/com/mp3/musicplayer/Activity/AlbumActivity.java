package com.mp3.musicplayer.Activity;

import android.app.Dialog;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Adapters.SongAdapter2;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;
import com.mp3.musicplayer.Utils.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class AlbumActivity extends AppCompatActivity implements View.OnClickListener, SongAdapter2.ClickListhener {

    private TextView albumName, totalSong, sortHeading, sortDone, sortCancel;

    private RadioButton sortByName, sortByArtist, sortByAlbum, sortByDuration, sortByDateAdded, sortByAscending, sortByDecending;
    private LinearLayout sortHolder, normal_landing, search_bar;
    private ImageButton btnshare, btnequalizer, btnsort, btnplay, btnsearch, btnsetting;
    private RecyclerView songView;
    private SongAdapter2 songAdapter;
    private Dialog sortTypeDialog;

    private Handler menuClick;
    private int ofset = 0;
    private String albumNameText = "";

    private MediaMetadataRetriever metaRetriver;
    private byte[] art;
    private ImageView album_art;
    private ImageButton btnSearchText, btnSearchClose;
    private EditText etSearchText;

    private NestedScrollView scrollView;
    private boolean scroll = false;

    private LinearLayout songListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        sortTypeDialog = new Dialog(this);
        sortTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sortTypeDialog.setContentView(R.layout.sort_type);
        initializeUI();

        albumNameText = getIntent().getStringExtra(Constant.ALBUM_NAME);
        metaRetriver = new MediaMetadataRetriever();
        menuClick = new Handler();
        if (isTablet(this)) {
            ofset = getResources().getDimensionPixelSize(R.dimen.menu_size1);
        } else {
            ofset = getResources().getDimensionPixelSize(R.dimen.menu_size);
        }
        songListContainer = findViewById(R.id.album_song_menu_container);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        songListContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (height - ofset)));
        scrollView =  findViewById(R.id.scrool_view);

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !scroll;
            }
        });


        initial();

        normal_landing = findViewById(R.id.ll_album_normal_landing);
        search_bar = findViewById(R.id.ll_album_search_bar);


        sortDone.setOnClickListener(this);
        sortCancel.setOnClickListener(this);


        btnSearchText =  findViewById(R.id.btn_album_search_text);
        btnSearchClose =  findViewById(R.id.btn_album_search_close);

        etSearchText =  findViewById(R.id.et_album_search_text);

        etSearchText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // Perform action on key press
                        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
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


        btnsearch.setOnClickListener(this);
        btnsetting.setOnClickListener(this);
        btnplay.setOnClickListener(this);
        btnsort.setOnClickListener(this);
        btnequalizer.setOnClickListener(this);
        btnshare.setOnClickListener(this);


        if (Constant.currentPlaylist.size() != 0) {
            totalSong.setText(Constant.TOTAL_TRACK_TEXT + Constant.currentPlaylist.size());
        }
        songView =  findViewById(R.id.album_list_view);


        //sort song

        Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });

        try {
            boolean recentlyAdded = getIntent().getBooleanExtra(Constant.RECENTLY_ADDED, false);

            if (recentlyAdded) {
                sortByDateAdded.setChecked(true);
                sortByDecending.setChecked(true);
                sortSong();
            }
        } catch (Exception e) {
        }

        showSong();


    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLanguage();
    }

    private void updateLanguage() {
        sortHeading.setText(getResources().getStringArray(R.array.Sort)[Constant.LANGUAGE_POSITION]);
        sortByName.setText(getResources().getStringArray(R.array.Title)[Constant.LANGUAGE_POSITION]);
        sortByArtist.setText(getResources().getStringArray(R.array.Artists)[Constant.LANGUAGE_POSITION]);
        sortByAlbum.setText(getResources().getStringArray(R.array.Albums)[Constant.LANGUAGE_POSITION]);
        sortByDuration.setText(getResources().getStringArray(R.array.Duration)[Constant.LANGUAGE_POSITION]);
        sortByDateAdded.setText(getResources().getStringArray(R.array.Date_added)[Constant.LANGUAGE_POSITION]);
        sortByAscending.setText(getResources().getStringArray(R.array.Short_in_Ascending_Order)[Constant.LANGUAGE_POSITION]);
        sortByDecending.setText(getResources().getStringArray(R.array.Short_in_descending_Order)[Constant.LANGUAGE_POSITION]);
        sortDone.setText(getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        sortCancel.setText(getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);

    }

    private void initializeUI() {
        albumName = findViewById(R.id.album_name);
        totalSong = findViewById(R.id.album_total_track);
        album_art = findViewById(R.id.album_album_art);

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
        btnshare = findViewById(R.id.btn_album_app_share);
        btnequalizer = findViewById(R.id.btn_album_equalizer);
        btnsort = findViewById(R.id.btn_album_sort_type);
        btnplay = findViewById(R.id.btn_album_play);
        btnsearch = findViewById(R.id.btn_album_search);
        btnsetting = findViewById(R.id.btn_album_setting);
    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    private void initial() {

        albumName.setText(albumNameText);

        Date date1 = new Date();
        Utilities util = new Utilities();

        long totalTimeSum = 0;

        for (int i = 0; i < Constant.currentPlaylist.size(); i++) {

            SimpleDateFormat sdf = new SimpleDateFormat(Constant.SIMPLEDATEFORMATE);
            sdf.setTimeZone(TimeZone.getTimeZone(Constant.TIMEZONE));


            try {
                if (Constant.currentPlaylist.get(i).getLength().length() < 6) {
                    date1 = sdf.parse("00:" + Constant.currentPlaylist.get(i).getLength());
                } else {
                    date1 = sdf.parse(Constant.currentPlaylist.get(i).getLength());
                }

                totalTimeSum = totalTimeSum + date1.getTime();

            } catch (Exception e) {
                // Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

        }

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Constant.currentPlaylist.get(0).getId());

        try {
            metaRetriver.setDataSource(this, trackUri);
            art = metaRetriver.getEmbeddedPicture();
            if (art != null) {
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                Drawable ob = new BitmapDrawable(getResources(), songImage);
                if (songImage != null) {
                    album_art.setImageDrawable(ob);
                    //ob = new BitmapDrawable(getResources(), blurFilter.fastblur(songImage, 1, 15));
                    //main_view.setBackground(ob);

                }
            } else {
                album_art.setImageResource(R.drawable.round_image);
                // BitmapDrawable ob1 = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.round_image), 1, 15));
                // main_view.setBackground(ob1);
            }
        } catch (Exception e) {

        }
    }

    private void showSong() {


        songAdapter = new SongAdapter2(this, Constant.currentPlaylist);
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

                if (Constant.currentPlaylist.size() > 1) {
                    Constant.currentPlaylist.remove(viewHolder.getAdapterPosition());
                    PreferenceManager.getInstance(getApplicationContext()).removePlaylistByname(albumNameText);
                    PreferenceManager.getInstance(getApplicationContext()).setPlayListByName(albumNameText,Constant.currentPlaylist);
                    showSong();
                    Toast.makeText(AlbumActivity.this,getApplicationContext().getResources().getString(R.string.songRemoved),Toast.LENGTH_SHORT).show();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(songView);
/*
        songView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                if (dy == 0) {
                    //llPlaylist.setBackgroundResource(R.color.null_color);
                    //fastScroller.setVisibility(View.GONE);

                } else {
                    //fastScroller.setVisibility(View.VISIBLE);
                    //llPlaylist.setBackgroundResource(R.drawable.recycleview_background);
                    menuClick.removeCallbacks(mHideScrollBar);
                    menuClick.postDelayed(mHideScrollBar, 3000);
                }

            }


        });*/

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
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getTitle().compareTo(rhs.getTitle());
                        }
                    });
                } else {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getTitle().compareTo(lhs.getTitle());
                        }
                    });
                }

                break;
            case 2:
                if (j == 1) {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getArtist().compareTo(rhs.getArtist());
                        }
                    });
                } else {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getArtist().compareTo(lhs.getArtist());
                        }
                    });
                }
                break;
            case 3:
                if (j == 1) {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getAlbum().compareTo(rhs.getAlbum());
                        }
                    });
                } else {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getAlbum().compareTo(lhs.getAlbum());
                        }
                    });
                }


                break;
            case 4:

                if (j == 1) {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getLength().compareTo(rhs.getLength());
                        }
                    });
                } else {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getLength().compareTo(lhs.getLength());
                        }
                    });
                }


                break;
            case 5:
                if (j == 1) {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return lhs.getDateAdded().compareTo(rhs.getDateAdded());
                        }
                    });
                } else {
                    Collections.sort(Constant.currentPlaylist, new Comparator<Song>() {

                        @Override
                        public int compare(Song lhs, Song rhs) {
                            return rhs.getDateAdded().compareTo(lhs.getDateAdded());
                        }
                    });
                }
                break;
        }

        sortTypeDialog.dismiss();
        showSong();

    }

    private void search() {
        final List<Song> searchSong = new ArrayList<Song>();
        for (int i = 0; i < Constant.currentPlaylist.size(); i++) {
            if (Constant.currentPlaylist.get(i).getTitle().toUpperCase().contains(etSearchText.getText().toString().toUpperCase()) || Constant.currentPlaylist.get(i).getArtist().toUpperCase().contains(etSearchText.getText().toString().toUpperCase())) {
                searchSong.add(new Song(Constant.currentPlaylist.get(i).getId(), Constant.currentPlaylist.get(i).getTitle(), Constant.currentPlaylist.get(i).getArtist(), Constant.currentPlaylist.get(i).getAlbum(), Constant.currentPlaylist.get(i).getGeneres(), Constant.currentPlaylist.get(i).getLength(), Constant.currentPlaylist.get(i).getDateAdded(), Constant.currentPlaylist.get(i).getData()));
            }
        }

        songAdapter = new SongAdapter2(this, searchSong);
        songAdapter.setClickListhener(new SongAdapter2.ClickListhener() {
            @Override
            public void ItemClick(View view, int position) {
                Constant.currentPlaylist = new ArrayList<Song>();
                Constant.currentPlaylist = searchSong;
                if (Constant.currentPlaylist != null) {
                    Constant.songPosition = position;
                    Constant.comeFromSongMenu = false;
                    startActivity(new Intent(AlbumActivity.this, PlayActivity.class));
                    finish();
                }
            }
        });

        // songAdapter.setLongClickListhener(this);
        songView.setAdapter(songAdapter);
        songView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_album_app_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Constant.PLAYSTORELINK + getPackageName());
                startActivity(Intent.createChooser(sharingIntent, Constant.SHARETITTLE));
                break;

            case R.id.btn_album_equalizer:
                startActivity(new Intent(this, EqualizerActivity.class));
                break;

            case R.id.btn_album_sort_type:
                sortTypeDialog.show();
                break;
            case R.id.btn_sort_cancel:
                sortTypeDialog.dismiss();
                break;
            case R.id.btn_sort_done:
                sortSong();
                break;

            case R.id.btn_album_play:
                if (Constant.currentPlaylist.size() != 0) {
                    Constant.comeFromSongMenu = false;
                    startActivity(new Intent(this, PlayActivity.class));
                    finish();
                }
                break;

            case R.id.btn_album_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.btn_album_search:
                normal_landing.setVisibility(View.GONE);
                search_bar.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translet_right));
                search_bar.setVisibility(View.VISIBLE);
                etSearchText.requestFocus();
                InputMethodManager imm3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm3.showSoftInput(etSearchText, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.btn_album_search_close:
                search_bar.setVisibility(View.GONE);
                normal_landing.startAnimation(AnimationUtils.loadAnimation(this, R.anim.translate_left));
                normal_landing.setVisibility(View.VISIBLE);
                etSearchText.setText("");
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(etSearchText.getWindowToken(), 0);
                showSong();
                break;
            case R.id.btn_album_search_text:
                search();
                break;
        }
    }

    @Override
    public void ItemClick(final View view, int position) {
        view.setBackgroundColor(getResources().getColor(R.color.item_click_background));
        menuClick.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setBackgroundColor(getResources().getColor(R.color.null_color));
            }
        }, 300);

        Constant.songPosition = position;
        Constant.comeFromSongMenu = false;
        startActivity(new Intent(this, PlayActivity.class));
        finish();
    }


}
