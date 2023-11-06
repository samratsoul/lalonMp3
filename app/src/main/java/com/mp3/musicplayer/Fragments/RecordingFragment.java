package com.mp3.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Activity.PlayActivity;
import com.mp3.musicplayer.Adapters.ArtistGenrsAlbumAdapter;
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
 * Use the {@link RecordingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordingFragment extends Fragment implements ArtistGenrsAlbumAdapter.ClickListhener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArtistGenrsAlbumAdapter recordingAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //srtist recycle view and adapter
    private RecyclerView recordingView;
    //list for artist
    private List<Artist_Genrs_Album> recording;

    private ArtistsFragment.OnFragmentInteractionListener mListener;
    private Cursor cursor;

    private Context context;

    public RecordingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordingFragment newInstance(String param1, String param2) {
        RecordingFragment fragment = new RecordingFragment();
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
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(com.mp3.musicplayer.R.layout.fragment_recording, container, false);
        recordingView = v.findViewById(R.id.recording_view);
        recording = new ArrayList<Artist_Genrs_Album>();
        recordingAdapter = new ArtistGenrsAlbumAdapter(getActivity(), recording);
        getRecording();
        // VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) v.findViewById(R.id.fast_scroller);
        //fastScroller.setRecyclerView(artistView);
        //artistView.setOnScrollListener(fastScroller.getOnScrollListener());


        //sorting artist
        Collections.sort(recording, new Comparator<Artist_Genrs_Album>() {
            @Override
            public int compare(Artist_Genrs_Album lhs, Artist_Genrs_Album rhs) {
                return lhs.getArtist().compareTo(rhs.getArtist());
            }
        });


        recordingAdapter = new ArtistGenrsAlbumAdapter(getActivity(), recording);

        //on click listhener
        recordingAdapter.setClickListhener(this);
        //artistAdapter.setLongClickListhener(this);
        recordingView.setAdapter(recordingAdapter);
        recordingView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return v;

    }


    private void getRecording() {

        Constant.currentPlaylist = new ArrayList<Song>();

        String selection = MediaStore.Audio.Media.DATA + " LIKE '%" + "Mp3_Player" + "%'";

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

        //query cursor for song
        cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Media.DATA + " like ? ",
                new String[]{"%Mp3_Player%"},
                null);

        //add the song on allsong l
        while (cursor.moveToNext()) {

            String genr = "<unknown>";

            try {
                Constant.currentPlaylist.add(new Song(Long.parseLong(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), genr, cursor.getString(4), cursor.getString(5), cursor.getString(6)));
                recording.add(new Artist_Genrs_Album(cursor.getString(1)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        Constant.totalSong = Constant.currentPlaylist.size() + "";
        recordingAdapter.notifyDataSetChanged();

    }


    @Override
    public void ItemClick(View view, int position) {

        //if not null than play
        if (Constant.currentPlaylist != null) {
            Constant.songPosition = position;
            Constant.comeFromSongMenu = false;
            startActivity(new Intent(getActivity(), PlayActivity.class));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}