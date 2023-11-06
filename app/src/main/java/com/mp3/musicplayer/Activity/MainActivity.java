package com.mp3.musicplayer.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.mp3.musicplayer.Adapters.MyPagerAdapter;
import com.mp3.musicplayer.Fragments.RateUsDialogFragment;
import com.mp3.musicplayer.Model.AllSong;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.BlurFilter;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import tabs.SlidingTabLayout;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 111;
    BlurFilter blurFilter = new BlurFilter();
    Animation animFadein;
    //view page adapter
    private ViewPager mpager;
    //slifingtab layout
    private SlidingTabLayout mtab;
    private MyPagerAdapter pagerAdapter;
    private LinearLayout mainContainer;
    //tho cursor for media fetching
    private Cursor cursor;
    private Cursor genresCursor;
    //private SharedPreferences sharedPreferences;
    private PreferenceManager mPreference;
    private int numberOfUse = 0;


    @Override
    public void onSuperCreate() {
        setContentView(R.layout.activity_main);
        initialView();
//        printHashKey(this);
    }


//    public static void printHashKey(Context pContext) {
//        try {
//            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e(TAG, "printHashKey()", e);
//        }
//    }

    private void showRatingDialog() {
        Log.d("rating", "showForRating : Showing translation dialog");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // We can get the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                    flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mPreference.setRateUsPressed(true);
//                                try {
//                                    Toast.makeText(MainActivity.this, "Review successfull", Toast.LENGTH_LONG).show();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }
                    });
                } else {
                    try {
                        Toast.makeText(this, Constant.ERROR_REVIEW, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prevDialog = getSupportFragmentManager().findFragmentByTag(RateUsDialogFragment.TAG);

            if (prevDialog != null) {
                ft.remove(prevDialog);
                Log.d(TAG, "showForRating : dialog is showing");
            }
            ft.addToBackStack(null);
            RateUsDialogFragment ratingDialog = RateUsDialogFragment.getInstance(this);
            ratingDialog.show(ft, RateUsDialogFragment.TAG);

        }
    }


    private void initialView() {
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mainContainer = findViewById(R.id.mainContainer);
        mPreference = PreferenceManager.getInstance(this);
        //getting langiage position and song path and filetr clip time from wsaved value
        Constant.LANGUAGE_POSITION = mPreference.getLanguagePositionSet();
        Constant.songPath = mPreference.getSongPath(Environment.getExternalStorageDirectory().getParentFile().getParentFile().getAbsolutePath());
        Constant.filterTime = mPreference.getCurrentTime("00:00");
        Constant.firstCreated = mPreference.isFirstTimeCreated();

        numberOfUse = mPreference.getNumberOfUses();

        if (!mPreference.isRateUsPressed()) {
            if (numberOfUse != 0 && (numberOfUse % 5) == 0) {
                showRatingDialog();
            }
        }
        if (numberOfUse <= 25000) {
            numberOfUse++;
            mPreference.setNumberOfUses(numberOfUse);
        }

        //Gson is for save special class in shared preference


        //String json = mPreference.getPlaylistMenu();


        //taking animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);

        Constant.name_playlist = mPreference.getPlaylistMenu();

        if (Constant.name_playlist == null) {
            Constant.name_playlist = new ArrayList<Artist_Genrs_Album>();
        }

        Constant.allSongs = new ArrayList<AllSong>();

        //permission and get song list
        if (checkPermission()) {
            //initial song and layout
            getAllSong();
            initial();

        } else {
            //request permission if not permitted
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.WAKE_LOCK, Manifest.permission.RECEIVE_BOOT_COMPLETED}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllSong();
                    initial();
                } else {
                    requestPermission();
                }
                break;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constant.languageChange) {
            Constant.languageChange = false;
            initial();
        }
    }

    private boolean checkPermission() {

        //the seven permission check
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }


    private void getAllSong() {

        Constant.allSongs.clear();
        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        //selection for current song path which filter current song
        String selection = MediaStore.Audio.Media.DATA + " LIKE '%" + Constant.songPath + "%'";

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

        //genres projection for gener
        String[] genresProjection = {
                MediaStore.Audio.Genres.NAME,
                MediaStore.Audio.Genres._ID
        };

        //String sortOrder = MediaStore.Audio.Media.TITLE + " DESC";
        //query cursor for song
        cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        Uri uri;
        //add the song on allsong l
        while (cursor.moveToNext()) {
            int musicId = Integer.parseInt(cursor.getString(0));

            int genre_column_index = 0;
            //if no genrs set than put untitle
            String genr = "<unknown>";
            try {
                uri = MediaStore.Audio.Genres.getContentUriForAudioId("external", musicId);
                genresCursor = this.getContentResolver().query(uri, genresProjection, null, null, null);
                assert genresCursor != null;
                genre_column_index = genresCursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME);
                if (genresCursor.moveToFirst()) {

                    do {
                        if (!genresCursor.getString(genre_column_index).trim().toLowerCase().equals("")) {
                            genr = genresCursor.getString(genre_column_index);
                            break;
                        }
                    } while (genresCursor.moveToNext());
                }

            } catch (Exception e) {

            } finally {
                genresCursor.close();
            }
            try {
                Constant.allSongs.add(new AllSong(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), genr, Long.parseLong(cursor.getString(4)), cursor.getString(5), cursor.getString(6)));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        //two date veriable for filter time
        Date date1 = new Date();
        Date date2 = new Date();

        for (int i = 0; i < Constant.allSongs.size(); i++) {

            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));


            try {
                if (Constant.allSongs.get(i).getLength().length() < 6) {
                    date1 = sdf.parse("00:" + Constant.allSongs.get(i).getLength());
                } else {
                    date1 = sdf.parse(Constant.allSongs.get(i).getLength());
                }
                date2 = sdf.parse("00:" + Constant.filterTime);

            } catch (Exception e) {
                // Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }

            //sort filter clip. remove the song from all file comparing filter clip

            if (date1.getTime() < date2.getTime()) {
                Constant.allSongs.remove(i);
                i--;
            }

        }
        Constant.totalSong = Constant.allSongs.size() + "";
    }


    private void initial() {

        if (numberOfUse <= 5) {
            Toast.makeText(this, "Long press items for more menu.", Toast.LENGTH_LONG).show();
        }
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
        mpager = findViewById(R.id.pager);
        try {
            mpager.setAdapter(pagerAdapter);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


        mtab = findViewById(R.id.tabs);


        mtab.setAnimation(animFadein);
        mtab.setDistributeEvenly(true);
        //change here background color
        // mtab.setBackgroundColor(getResources().getColor(R.color.imagebackground));
        mtab.setSelectedIndicatorColors(getResources().getColor(R.color.none));
        mtab.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        mtab.setViewPager(mpager);
        //make current postion to song page
        mpager.setCurrentItem(2);
        // mpager.setAnimation(animFadein);
        mtab.animate();
        mpager.setAnimation(animFadein);


        //blur background image
        /*
        BitmapDrawable ob = new BitmapDrawable(getResources(), blurFilter.fastblur(BitmapFactory.decodeResource(getResources(), R.drawable.main_background), 1, 15));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mainContainer.setBackground(ob);
        } else {
            mainContainer.setBackgroundResource(R.drawable.main_background);
        }*/

    }


    /*
        private void saveLastPlayList() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(Constant.currentPlaylist);
            editor.putString(Constant.LAST_PLAYED_MENU, json);
            editor.commit();
        }
    */

    public void finishActivity() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (Constant.musicPlaying) {
            moveTaskToBack(true);
        } else {
            System.exit(0);
            super.onBackPressed();
        }
    }


    @Override
    public void onSuperDestroy() {
        Constant.allSongs.clear();
        //System.exit(0);
        super.onDestroy();
    }


}
