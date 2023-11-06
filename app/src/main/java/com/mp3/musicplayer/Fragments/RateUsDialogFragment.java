package com.mp3.musicplayer.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.mp3.musicplayer.R;
import com.mp3.musicplayer.Utils.PreferenceManager;
import com.mp3.musicplayer.Utils.Utilities;

public class RateUsDialogFragment extends DialogFragment {
    public static final String TAG = RateUsDialogFragment.class.getSimpleName();
    private static RateUsDialogFragment mInstance = null;
    PreferenceManager preferenceManager = null;
    Drawable selectedImage = null;
    Drawable notSelectedImage = null;
    ImageView ivRatingbarAnimator1,ivRatingbarAnimator2,ivRatingbarAnimator3,ivRatingbarAnimator4,ivRatingbarAnimator5;
    TextView ratingBarStatus;
    Button btn_rate_submit,btn_rate_never,btn_rate_not_now;
    private RateUsDialogFragment(Context context) {

    }

    public static RateUsDialogFragment getInstance(Context context) {
        if (mInstance == null)
            return new RateUsDialogFragment(context);
        else
            return mInstance;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferenceManager = PreferenceManager.getInstance(getActivity());
        selectedImage = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_star_selected,null);
        notSelectedImage = ResourcesCompat.getDrawable(getResources(),R.drawable.ic_star_selected,null);
        ivRatingbarAnimator1 = view.findViewById(R.id.ivRatingbarAnimator1);
        ivRatingbarAnimator2 = view.findViewById(R.id.ivRatingbarAnimator2);
        ivRatingbarAnimator3 = view.findViewById(R.id.ivRatingbarAnimator3);
        ivRatingbarAnimator4 = view.findViewById(R.id.ivRatingbarAnimator4);
        ivRatingbarAnimator5 = view.findViewById(R.id.ivRatingbarAnimator5);
        btn_rate_not_now = view.findViewById(R.id.btn_rate_not_now);
        btn_rate_never = view.findViewById(R.id.btn_rate_never);
        btn_rate_submit = view.findViewById(R.id.btn_rate_submit);
        ratingBarStatus = view.findViewById(R.id.tvRatingStatus);
        ivRatingbarAnimator1.setOnClickListener(v->{
            ratingBarStatus.setText( "Very bad");
            setImageForRatingBar(ivRatingbarAnimator1,true);
            setImageForRatingBar(ivRatingbarAnimator2,false);
            setImageForRatingBar(ivRatingbarAnimator3,false);
            setImageForRatingBar(ivRatingbarAnimator4,false);
            setImageForRatingBar(ivRatingbarAnimator5,false);
        });
        ivRatingbarAnimator2.setOnClickListener(v->{
            ratingBarStatus.setText( "Bad");
            setImageForRatingBar(ivRatingbarAnimator1,true);
            setImageForRatingBar(ivRatingbarAnimator2,true);
            setImageForRatingBar(ivRatingbarAnimator3,false);
            setImageForRatingBar(ivRatingbarAnimator4,false);
            setImageForRatingBar(ivRatingbarAnimator5,false);
        });
        ivRatingbarAnimator3.setOnClickListener(v->{
            ratingBarStatus.setText( "Average");
            setImageForRatingBar(ivRatingbarAnimator1,true);
            setImageForRatingBar(ivRatingbarAnimator2,true);
            setImageForRatingBar(ivRatingbarAnimator3,true);
            setImageForRatingBar(ivRatingbarAnimator4,false);
            setImageForRatingBar(ivRatingbarAnimator5,false);
        });
        ivRatingbarAnimator4.setOnClickListener(v->{
            ratingBarStatus.setText( "Good");
            setImageForRatingBar(ivRatingbarAnimator1,true);
            setImageForRatingBar(ivRatingbarAnimator2,true);
            setImageForRatingBar(ivRatingbarAnimator3,true);
            setImageForRatingBar(ivRatingbarAnimator4,true);
            setImageForRatingBar(ivRatingbarAnimator5,false);
        });
        ivRatingbarAnimator5.setOnClickListener(v->{
            ratingBarStatus.setText( "Excellent");
            setImageForRatingBar(ivRatingbarAnimator1,true);
            setImageForRatingBar(ivRatingbarAnimator2,true);
            setImageForRatingBar(ivRatingbarAnimator3,true);
            setImageForRatingBar(ivRatingbarAnimator4,true);
            setImageForRatingBar(ivRatingbarAnimator5,true);
        });
        btn_rate_submit.setOnClickListener(v ->
                didClickForRating()
        );
        btn_rate_never.setOnClickListener(v -> {
            preferenceManager.setRateUsPressed(true);
            dismiss();
        });
        btn_rate_not_now.setOnClickListener(v -> {
            dismiss();
        });

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FloatingDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_rate_us, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            getDialog().getWindow().setWindowAnimations(R.style.dialog_anim);
        }
    }

    void setImageForRatingBar(ImageView image,Boolean selected){
        if(selected){
            image.setImageDrawable(selectedImage);
        }else{
            image.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_nav_star,null));
        }
    }

    private void didClickForRating() {
        Utilities.launchAppForRating(getActivity());
        preferenceManager.setRateUsPressed(true);
        dismiss();
    }
}
