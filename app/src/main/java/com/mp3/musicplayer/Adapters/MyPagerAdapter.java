package com.mp3.musicplayer.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mp3.musicplayer.Fragments.AlbumsFragment;
import com.mp3.musicplayer.Fragments.ArtistsFragment;
import com.mp3.musicplayer.Fragments.GenresFragment;
import com.mp3.musicplayer.Fragments.PlaylistFragment;
import com.mp3.musicplayer.Fragments.RecordingFragment;
import com.mp3.musicplayer.Fragments.SongFragmement;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    String[] tabs;
    private Context contex;
    private GenresFragment genresFragment;
    private PlaylistFragment playlistFragment;
    private SongFragmement songFragmement;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;
    private RecordingFragment recordingFragment;
    //private MyFilesFragment myFilesFragment;

    public MyPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        contex = context;
        tabs = contex.getResources().getStringArray(com.mp3.musicplayer.R.array.tabsName_English);
        switch (Constant.LANGUAGE_POSITION) {
            case 0:
                tabs = contex.getResources().getStringArray(R.array.tabsName_English);
                break;
            case 1:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Hindi);
                break;
            case 2:
                tabs = contex.getResources().getStringArray(R.array.tabsName_French);
                break;
            case 3:
                tabs = contex.getResources().getStringArray(R.array.tabsName_German);
                break;
            case 4:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Chinese);
                break;
            case 5:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Spanish);
                break;
            case 6:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Japanese);
                break;
            case 7:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Russian);
                break;
            case 8:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Arabic);
                break;
            case 9:
                tabs = contex.getResources().getStringArray(R.array.tabsName_Portuguese);
                break;

        }

        genresFragment = new GenresFragment();
        playlistFragment = new PlaylistFragment();
        songFragmement = new SongFragmement();
        artistsFragment = new ArtistsFragment();
        albumsFragment = new AlbumsFragment();
        recordingFragment= new RecordingFragment();
       // myFilesFragment = new MyFilesFragment();

    }


    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return genresFragment;
            case 1:
                return playlistFragment;
            case 2:
                return songFragmement;
            case 3:
                return artistsFragment;
            case 4:
                return recordingFragment;
            case 5:
                return albumsFragment;
//            case 6:
//                return myFilesFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 6;
    }
}