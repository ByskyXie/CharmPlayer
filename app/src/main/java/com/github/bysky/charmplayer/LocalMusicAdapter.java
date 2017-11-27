package com.github.bysky.charmplayer;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by asus on 2017/11/26.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicHolder> {
    private OnItemClickListener listener;
    private Context context;
    private Cursor dbList;

    static class LocalMusicHolder extends RecyclerView.ViewHolder{
        TextView textViewMusicName;
        TextView textViewMusicArtist;
        View layoutPlay;
        View itemView;

        LocalMusicHolder(View itemView) {
            super(itemView);
            textViewMusicName = itemView.findViewById(R.id.item_local_music_name);
            textViewMusicArtist = itemView.findViewById(R.id.item_local_music_artist);
            layoutPlay = itemView.findViewById(R.id.item_local_music_layout_play);
            this.itemView = itemView;
        }
    }

    LocalMusicAdapter(Context context, Cursor dbList, OnItemClickListener listener){
        this.context = context;
        this.dbList = dbList;
        this.listener = listener;
    }

    @Override
    public LocalMusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music,parent,false);
        return new LocalMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalMusicHolder holder, final int position) {
        if(!dbList.moveToPosition(position)){
            Log.e("MusicAdapter","错误的列表位置");
            return;
        }
        holder.textViewMusicName.setText(dbList.getString(dbList.getColumnIndex("MUSIC_NAME")));
        holder.textViewMusicArtist.setText(dbList.getString(dbList.getColumnIndex("ARTIST")));

        //监听器
        holder.layoutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(context,holder.getAdapterPosition());
            }
        });
        //设置icon长宽
        int size = (int)(context.getResources().getDisplayMetrics().widthPixels * 1.0 / 13);
        ViewGroup.LayoutParams params;
        params = holder.itemView.findViewById(R.id.item_local_music_add).getLayoutParams();
        params.width = params.height = size;
        params = holder.itemView.findViewById(R.id.item_local_music_more).getLayoutParams();
        params.width = params.height = size;
        holder.itemView.getLayoutParams().height =
                (int)(context.getResources().getDisplayMetrics().heightPixels * 1.0 / 11);
    }

    @Override
    public int getItemCount() {
        return dbList.getCount();
    }

    interface OnItemClickListener{
        void onClick(Context context,int position);
    }

}
