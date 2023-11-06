package com.mp3.musicplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Activity.AlbumActivity;
import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
import com.mp3.musicplayer.Model.AllSong;
import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaylistFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlaylistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistFragment extends Fragment implements ArtistGenrsAlbumAdapter.ClickListhener, ArtistGenrsAlbumAdapter.LongClickListhener, View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView btnDeleteOK, btnDeleteCancel;
    ArtistGenrsAlbumAdapter playlistAdapter;
    private int playlistPosition = 0;
    private LinearLayout delete_menu;
    private RecyclerView playlistView;
    private List<Artist_Genrs_Album> playlist;
    private Dialog deleteDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PlaylistFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlaylistFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlaylistFragment newInstance(String param1, String param2) {
        PlaylistFragment fragment = new PlaylistFragment();
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
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        deleteDialog = new Dialog(getActivity());
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.delete_confirm);

        delete_menu =  deleteDialog.findViewById(R.id.ll_delete_menu);


        //delete menu

        btnDeleteCancel =  deleteDialog.findViewById(R.id.btn_delete_cancel);
        btnDeleteOK =  deleteDialog.findViewById(R.id.btn_delete_ok);


        btnDeleteCancel.setOnClickListener(this);
        btnDeleteOK.setOnClickListener(this);

        if (Constant.firstCreated) {
            Constant.name_playlist.add(new Artist_Genrs_Album(Constant.ALBUM_FAVOURITE));
            Constant.name_playlist.add(new Artist_Genrs_Album(Constant.ALBUM_HOME));
            Constant.name_playlist.add(new Artist_Genrs_Album(Constant.ALBUM_OFFICE));
            Constant.name_playlist.add(new Artist_Genrs_Album(Constant.ALBUM_TRAVEL));

            PreferenceManager.getInstance(getActivity()).setPlaylistMenu(Constant.name_playlist);
            PreferenceManager.getInstance(getActivity()).setRepeat(2);
            PreferenceManager.getInstance(getActivity()).setISFirstTimeCreated(false);

        }

        playlistView =  v.findViewById(R.id.playlist_view);
        playlist = new ArrayList<Artist_Genrs_Album>();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        btnDeleteOK.setText(getActivity().getResources().getStringArray(R.array.Done)[Constant.LANGUAGE_POSITION]);
        btnDeleteCancel.setText(getActivity().getResources().getStringArray(R.array.Cancel)[Constant.LANGUAGE_POSITION]);


        // VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.fast_scroller);
        // fastScroller.setRecyclerView(playlistView);
        // playlistView.setOnScrollListener(fastScroller.getOnScrollListener());
        getPlaylist();
        playlistAdapter = new ArtistGenrsAlbumAdapter(getActivity(), playlist);
        playlistAdapter.setClickListhener(this);
        playlistAdapter.setLongClickListhener(this);
        playlistView.setAdapter(playlistAdapter);
        playlistView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void getPlaylist() {

        playlist.clear();

        Constant.name_playlist = PreferenceManager.getInstance(getActivity()).getPlaylistMenu();
        if (Constant.name_playlist == null) {
            Constant.name_playlist = new ArrayList<Artist_Genrs_Album>();
        }

        //first two are define
        playlist.add(new Artist_Genrs_Album(Constant.LAST_PLAYED_TEXT));
        playlist.add(new Artist_Genrs_Album(Constant.RECENTLY_ADDED_TEXT));

        //get playlist name
        if (Constant.name_playlist.size() > 0) {
            for (int i = 0; i < Constant.name_playlist.size(); i++) {
                playlist.add(Constant.name_playlist.get(i));
            }
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

    @Override
    public void ItemClick(View view, int position) {
        Constant.comeFromSongMenu = false;
        Constant.songPosition = 0;
        Intent intent = new Intent(getActivity(), AlbumActivity.class);
        intent.putExtra(Constant.ALBUM_NAME, playlist.get(position).getArtist());

        //last played
        if (position == 0) {
            Constant.currentPlaylist = PreferenceManager.getInstance(getActivity()).getLastPlayedMenuItems();

            if (Constant.currentPlaylist == null) {
                Constant.currentPlaylist = new ArrayList<Song>();
            }

            if (Constant.currentPlaylist.size() > 0) {
                startActivity(intent);
            }


        } else if (position == 1) {
            //filter recently added

            Constant.currentPlaylist = new ArrayList<Song>();
            List<AllSong> all = Constant.allSongs;

            //short date added
            Collections.sort(all, new Comparator<AllSong>() {
                @Override
                public int compare(AllSong lhs, AllSong rhs) {
                    return rhs.getDateAdded().compareTo(lhs.getDateAdded());
                }
            });

            for (int i = 0; i < all.size(); i++) {
                Constant.currentPlaylist.add(new Song(all.get(i).getId(), all.get(i).getTitle(), all.get(i).getArtist(), all.get(i).getAlbum(),
                        all.get(i).getGeneres(), all.get(i).getLength(), all.get(i).getDateAdded(), all.get(i).getData()));

            }

            if (Constant.currentPlaylist != null) {
                intent.putExtra(Constant.RECENTLY_ADDED, true);
                startActivity(intent);
            }
        } else {
            //TODO ASSIEND TO SHAMRAT HASAN, DIFFICULT TO UNDERSTAND THIS LOGIC

            //playlist from saved
            //Constant.name_playlist.get(position-2).getArtist();

            Constant.currentPlaylist = PreferenceManager.getInstance(getActivity()).getPlayListSecondPosition(position);

            if (Constant.currentPlaylist == null) {
                Constant.currentPlaylist = new ArrayList<Song>();
            }

            if (Constant.currentPlaylist.size() > 0) {
                intent.putExtra(Constant.ISPLAYLIST, true);
                intent.putExtra(Constant.PLALISTNAME, Constant.name_playlist.get(position - 2).getArtist());
                startActivity(intent);
            }else{
                try{
                    Toast.makeText(getActivity(), Constant.PLAYLIST_BLANK_INFO,Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void ItemLongClick(View view, int position) {
        if (position > 1) {
            playlistPosition = position;
            deleteDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_delete_ok:
                PreferenceManager.getInstance(getActivity()).removePlayListSecondPosition(playlistPosition);
                PreferenceManager.getInstance(getActivity()).setPlaylistMenu(Constant.name_playlist);
                getPlaylist();
                playlistAdapter = new ArtistGenrsAlbumAdapter(getActivity(), playlist);
                playlistAdapter.setClickListhener(this);
                playlistAdapter.setLongClickListhener(this);
                playlistView.setAdapter(playlistAdapter);
                playlistView.setLayoutManager(new LinearLayoutManager(getActivity()));

                deleteDialog.dismiss();
                break;
            case R.id.btn_delete_cancel:
                deleteDialog.dismiss();
                break;
        }
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
