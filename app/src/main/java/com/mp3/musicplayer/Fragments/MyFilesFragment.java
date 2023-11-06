package com.mp3.musicplayer.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mp3.musicplayer.Activity.DirectoryPickerActivity;
import com.mp3.musicplayer.Activity.MainActivity;
import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.Constant;
import com.mp3.musicplayer.Utils.PreferenceManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFilesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFilesFragment extends Fragment implements View.OnClickListener {
    public static final String CHOSEN_DIRECTORY = "chosenDir";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView fileLocation;
    private TextView total_file;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyFilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFilesFragment newInstance(String param1, String param2) {
        MyFilesFragment fragment = new MyFilesFragment();
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
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_my_files, container, false);
        fileLocation =  v.findViewById(R.id.file_location);
        total_file =  v.findViewById(R.id.total_song);

        fileLocation.setOnClickListener(this);
        fileLocation.setText(Constant.songPath);
        total_file.setOnClickListener(this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        total_file.setText(Constant.totalSong + " " + getActivity().getResources().getStringArray(R.array.songs)[Constant.LANGUAGE_POSITION]);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_location:
                startActivityForResult(new Intent(getActivity(), DirectoryPickerActivity.class), 321);
                break;
            case R.id.total_song:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 321) {
            try {
                fileLocation.setText(data.getStringExtra(CHOSEN_DIRECTORY));
                if (fileLocation.getText() != null) {
                    Constant.songPath = fileLocation.getText().toString();
                    PreferenceManager.getInstance(getActivity()).setSongPath(Constant.songPath);
                    startActivity(new Intent(getActivity(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                }
            } catch (Exception e) {

            }


        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
