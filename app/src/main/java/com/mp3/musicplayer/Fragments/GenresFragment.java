package com.mp3.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Activity.AlbumActivity;
import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
import com.mp3.musicplayer.Adapters.GeneresAdapter;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenresFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenresFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenresFragment extends Fragment implements GeneresAdapter.ClickListhener, ArtistGenrsAlbumAdapter.ClickListhener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArtistGenrsAlbumAdapter genresAdapter;
    //recycle view and adapter and list for generes
    private RecyclerView generView;
    private List<Artist_Genrs_Album> genres;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GenresFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenresFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenresFragment newInstance(String param1, String param2) {
        GenresFragment fragment = new GenresFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_genres, container, false);

        generView =  v.findViewById(R.id.gener_view);
        genres = new ArrayList<Artist_Genrs_Album>();

        //get generes
        getGenres();
        // VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.fast_scroller);
        // fastScroller.setRecyclerView(generView);
        // generView.setOnScrollListener(fastScroller.getOnScrollListener());


        //sorting gener
        Collections.sort(genres, new Comparator<Artist_Genrs_Album>() {
            @Override
            public int compare(Artist_Genrs_Album lhs, Artist_Genrs_Album rhs) {
                return lhs.getArtist().compareTo(rhs.getArtist());
            }
        });


        genresAdapter = new ArtistGenrsAlbumAdapter(getActivity(), genres);
        genresAdapter.setClickListhener(this);
        //artistAdapter.setLongClickListhener(this);
        generView.setAdapter(genresAdapter);
        generView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    private void getGenres() {

        //take all the genres
        for (int i = 0; i < Constant.allSongs.size(); i++) {
            genres.add(new Artist_Genrs_Album(Constant.allSongs.get(i).getGeneres()));
        }


        //removing duplicate
        for (int j = 0; j < genres.size(); j++) {

            for (int k = j + 1; k < genres.size(); k++) {

                if (genres.get(j).getArtist().equals(genres.get(k).getArtist())) {
                    genres.remove(k);
                    k--;
                }


            }
        }


    }


    @Override
    public void ItemClick(View view, int position) {
        //song will go from here

        Constant.currentPlaylist = new ArrayList<Song>();
        //selected genres
        String currentGeners = genres.get(position).getArtist();

        //creating playlist of  selected genrs song
        for (int i = 0; i < Constant.allSongs.size(); i++) {
            if (currentGeners.equals(Constant.allSongs.get(i).getGeneres())) {
                Constant.currentPlaylist.add(new Song(Constant.allSongs.get(i).getId(), Constant.allSongs.get(i).getTitle(), Constant.allSongs.get(i).getArtist(), Constant.allSongs.get(i).getAlbum(),
                        Constant.allSongs.get(i).getGeneres(), Constant.allSongs.get(i).getLength(), Constant.allSongs.get(i).getDateAdded(), Constant.allSongs.get(i).getData()));
            }
        }
        if (Constant.currentPlaylist != null) {
            Constant.songPosition = 0;
            Intent intent = new Intent(getActivity(), AlbumActivity.class);
            intent.putExtra(Constant.ALBUM_NAME, genres.get(position).getArtist());
            Constant.comeFromSongMenu = false;
            startActivity(intent);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
