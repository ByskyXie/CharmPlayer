package com.github.bysky.charmplayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by asus on 2017/12/13.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistHolder> {
    private Cursor artist;
    private Context context;

    class ArtistHolder extends RecyclerView.ViewHolder{
        private TextView textViewAritst;
        private TextView textViewCount;
        private View root;
        private ImageView imgView;
        ArtistHolder(View itemView) {
            super(itemView);
            imgView= itemView.findViewById(R.id.image_view_item_artist);
            textViewAritst = itemView.findViewById(R.id.text_view_item_artist);
            textViewCount = itemView.findViewById(R.id.text_view_item_count);
            root = itemView;
        }
    }

    public ArtistAdapter(Context context,Cursor artist) {
        this.artist = artist;
        this.context = context;
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_artist,parent,false);
        return new ArtistHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArtistHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        if(!artist.moveToPosition(pos))
            Log.e("ArtistAdapter","Failed to get Cursor'Artist' position.\n");
        holder.textViewAritst.setText(artist
                .getString(artist.getColumnIndex("ARTIST")));
        holder.textViewCount.setText("共"+ artist.getInt(artist.getColumnIndex("NUM")) +"项");
        //调整大小
        ViewGroup.LayoutParams params = holder.imgView.getLayoutParams();
        params.height = params.width = context.getResources().getDisplayMetrics().widthPixels/7;
        //监听器
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LocalMusicActivity.class);
                intent.putExtra("ARTIST",holder.textViewAritst.getText());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return artist.getCount();
    }
}
