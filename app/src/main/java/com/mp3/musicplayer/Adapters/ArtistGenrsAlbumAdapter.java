package com.mp3.musicplayer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mp3.musicplayer.Model.Artist_Genrs_Album;
import com.mp3.musicplayer.R;

import java.util.Collections;
import java.util.List;

public class ArtistGenrsAlbumAdapter extends RecyclerView.Adapter<ArtistGenrsAlbumAdapter.MyViewHolder> {

    List<Artist_Genrs_Album> data = Collections.emptyList();
    ClickListhener clickListhener;
    LongClickListhener longClickListhener;
    Context context;
    private LayoutInflater inflater;

    public ArtistGenrsAlbumAdapter(Context context, List<Artist_Genrs_Album> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.artist, viewGroup, false);
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
        Artist_Genrs_Album current = data.get(i);
        viewHolder.songArtist.setText(current.getArtist());
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
        TextView songArtist;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            songArtist = itemView.findViewById(R.id.artist_title);
        }

        @Override
        public void onClick(View v) {
            if (clickListhener != null) {
                clickListhener.ItemClick(v, getAdapterPosition());
            }
        }


        @Override
        public boolean onLongClick(View v) {
            if (longClickListhener != null) {
                longClickListhener.ItemLongClick(v, getAdapterPosition());
                return true;
            }
            return false;
        }
    }
}


