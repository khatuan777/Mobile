package com.example.lab05_music;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHoler> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<Data> songList;

    private OnItemClickListener itemClickListener;
    private Context context;

    public MusicAdapter(List<Data> songList, OnItemClickListener itemClickListener, Context context) {
        this.songList = songList;
        this.itemClickListener = itemClickListener;
        this.context = context;
    }


    class ViewHoler extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private CardView cardView;

        private Context context;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_nameSong);
        }
    }

    @NonNull
    @Override
    public ViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        return new ViewHoler(view);
    }

    //Số lượng
    @Override
    public int getItemCount() {
        return songList.size();
    }

    //
    @Override
    public void onBindViewHolder(@NonNull ViewHoler holder, int position) {
        Data song = songList.get(position);
        holder.cardView.setOnClickListener(v -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });
        holder.imageView.setImageResource(song.getImage());
        holder.textView.setText(song.getName());
    }


}
