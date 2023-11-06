package com.mp3.musicplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Model.Genres;
import com.mp3.musicplayer.R;

import java.util.Collections;
import java.util.List;

public class GeneresAdapter extends RecyclerView.Adapter<GeneresAdapter.MyViewHolder> {

    List<Genres> data = Collections.emptyList();
    ClickListhener clickListhener;
    Context context;
    private LayoutInflater inflater;

    public GeneresAdapter(Context context, List<Genres> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.genres, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    public void setClickListhener(ClickListhener clickListhener) {
        this.clickListhener = clickListhener;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Genres current = data.get(i);
        viewHolder.genresTittle.setText(current.getGeneres());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface ClickListhener {
        public void ItemClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView genresTittle;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            genresTittle = (TextView) itemView.findViewById(R.id.genresTittle);
        }

        @Override
        public void onClick(View v) {
            if (clickListhener != null) {
                clickListhener.ItemClick(v, getPosition());
            }
        }
    }
}
