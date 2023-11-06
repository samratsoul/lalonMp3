package com.mp3.musicplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Model.Song;
import com.mp3.musicplayer.R;

import java.util.Collections;
import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MyViewHolder> {

    List<Song> data = Collections.emptyList();
    ClickListhener clickListhener;
    LongClickListhener longClickListhener;
    Context context;
    private LayoutInflater inflater;

    public SongAdapter(Context context, List<Song> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.song, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    public void setClickListhener(ClickListhener clickListhener) {
        this.clickListhener = clickListhener;
    }

    public void setLongClickListhener(LongClickListhener longClickListhener) {
        this.longClickListhener = longClickListhener;
    }


    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Song current = data.get(i);
        viewHolder.songTitle.setText(current.getTitle());
        viewHolder.songArtist.setText("by " + current.getArtist());
        viewHolder.songDuration.setText(current.getLength());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public interface ClickListhener {
        public void ItemClick(View view, int position);
    }

    public interface LongClickListhener {
        public void ItemLongClick(View view, int position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView songTitle, songArtist, songDuration;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            songTitle = (TextView) itemView.findViewById(R.id.song_title);
            songArtist = (TextView) itemView.findViewById(R.id.song_artist);
            songDuration = (TextView) itemView.findViewById(R.id.song_duration);
        }


        @Override
        public void onClick(View v) {
            if (clickListhener != null) {
                clickListhener.ItemClick(v, getPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            if (longClickListhener != null) {
                longClickListhener.ItemLongClick(v, getPosition());
                return true;
            }
            return false;
        }


    }

}
