package com.mp3.musicplayer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Activity.AlbumActivity;
import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumsFragment extends Fragment implements ArtistGenrsAlbumAdapter.ClickListhener {

    //recycle view adapter
    ArtistGenrsAlbumAdapter albumAdapter;
    //recycle view for album view
    private RecyclerView albumView;
    private List<Artist_Genrs_Album> album;

    public AlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(com.mp3.musicplayer.R.layout.fragment_albums, container, false);
        // Inflate the layout for this fragment

//initialization
        albumView =  v.findViewById(R.id.album_view);
        album = new ArrayList<Artist_Genrs_Album>();
        //get album from allsong
        getAlbum();
        // VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.fast_scroller);
        // fastScroller.setRecyclerView(albumView);
        //albumView.setOnScrollListener(fastScroller.getOnScrollListener());


        //short song
        Collections.sort(album, new Comparator<Artist_Genrs_Album>() {
            @Override
            public int compare(Artist_Genrs_Album lhs, Artist_Genrs_Album rhs) {
                return lhs.getArtist().compareTo(rhs.getArtist());
            }
        });


        albumAdapter = new ArtistGenrsAlbumAdapter(getActivity(), album);

        //on click lishtener
        albumAdapter.setClickListhener(this);
        //artistAdapter.setLongClickListhener(this);
        albumView.setAdapter(albumAdapter);
        albumView.setLayoutManager(new GridLayoutManager(getActivity(),1));
        return v;
    }


    private void getAlbum() {

        //take all the album first form all song
        for (int i = 0; i < Constant.allSongs.size(); i++) {
            album.add(new Artist_Genrs_Album(Constant.allSongs.get(i).getAlbum()));
        }

        // remove duplicate song from all song
        for (int j = 0; j < album.size(); j++) {
            for (int k = j + 1; k < album.size(); k++) {
                if (album.get(j).getArtist().equals(album.get(k).getArtist())) {
                    album.remove(k);
                    k--;
                }
            }
        }
    }

    @Override
    public void ItemClick(View view, int position) {
        Constant.currentPlaylist = new ArrayList<Song>();
        //taking selected album name
        String currentAlbum = album.get(position).getArtist();
        //making playlist of song wich contain the album name
        for (int i = 0; i < Constant.allSongs.size(); i++) {
            if (currentAlbum.equals(Constant.allSongs.get(i).getAlbum())) {
                Constant.currentPlaylist.add(new Song(Constant.allSongs.get(i).getId(), Constant.allSongs.get(i).getTitle(), Constant.allSongs.get(i).getArtist(), Constant.allSongs.get(i).getAlbum(),
                        Constant.allSongs.get(i).getGeneres(), Constant.allSongs.get(i).getLength(), Constant.allSongs.get(i).getDateAdded(), Constant.allSongs.get(i).getData()));
            }
        }
        //if not blank than start
        if (Constant.currentPlaylist != null) {
            Constant.songPosition = 0;
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra(Constant.ALBUM_NAME, album.get(position).getArtist());
            Constant.comeFromSongMenu = false;
            startActivity(intent);
        }
    }
}
