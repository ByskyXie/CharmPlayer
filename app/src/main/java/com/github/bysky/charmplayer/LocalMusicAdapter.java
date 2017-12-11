package com.github.bysky.charmplayer;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by asus on 2017/11/26.
 */

public class LocalMusicAdapter extends RecyclerView.Adapter<LocalMusicAdapter.LocalMusicHolder> {
    private OnItemClickListener listener;
    private Context context;
    private Cursor dbList;
    private ArrayList<Music> musicList = new ArrayList<Music>();

    static class LocalMusicHolder extends RecyclerView.ViewHolder {
        TextView textViewMusicName;
        TextView textViewMusicArtist;
        ImageView imgAdd;
        ImageView imgMore;
        View layoutPlay;
        View itemView;

        LocalMusicHolder(View itemView) {
            super(itemView);
            textViewMusicName = itemView.findViewById(R.id.item_local_music_name);
            textViewMusicArtist = itemView.findViewById(R.id.item_local_music_artist);
            layoutPlay = itemView.findViewById(R.id.item_local_music_layout_play);
            imgAdd = itemView.findViewById(R.id.item_local_music_add);
            imgMore = itemView.findViewById(R.id.item_local_music_more);
            this.itemView = itemView;
        }
    }

    LocalMusicAdapter(Context context, Cursor dbList, OnItemClickListener listener) {
        this.context = context;
        this.dbList = dbList;
        this.listener = listener;
        String fileFolder,musicName,filePath,fileName,artist;
        if (dbList.moveToFirst()) {
            do {
                filePath = dbList.getString(dbList.getColumnIndex("FILE_PATH"));
                fileName = dbList.getString(dbList.getColumnIndex("FILE_NAME"));
                fileFolder = dbList.getString(dbList.getColumnIndex("FILE_FOLDER"));
                musicName = dbList.getString(dbList.getColumnIndex("MUSIC_NAME"));
                artist = dbList.getString(dbList.getColumnIndex("ARTIST"));
                musicList.add(new Music(filePath,fileName,fileFolder,musicName,artist));
            } while (dbList.moveToNext());
        }
    }

    @Override
    public LocalMusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music, parent, false);
        return new LocalMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalMusicHolder holder, final int position) {
        if (musicList.size() <= holder.getAdapterPosition()) {
            Log.e("MusicAdapter", "错误的列表位置");
            return;
        }
        String fileName = musicList.get(holder.getAdapterPosition()).getFileName();
        //通过正则检测是否符合【歌手 - 歌曲名】规范
        if (fileName.matches(".+[ ]+[-]{1}[ ]+.+")) {
            int temp = fileName.indexOf('-');
            holder.textViewMusicArtist.setText(fileName.substring(0, temp));
            //去除多余空格
            while (fileName.charAt(temp + 1) == ' ')
                temp++;
            holder.textViewMusicName.setText(fileName.substring(temp + 1));
        } else {
            holder.textViewMusicName.setText(fileName);
            holder.textViewMusicArtist.setText("未知歌手");
        }
        //TODO:怎么实现同步进行？还有再次点击暂停
//        changeChildViewState(holder);
        //监听器
        holder.layoutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(LocalMusicAdapter.this, holder);
            }
        });
        //设置icon长宽
        int size = (int) (context.getResources().getDisplayMetrics().widthPixels * 1.0 / 13);
        ViewGroup.LayoutParams params;
        params = holder.itemView.findViewById(R.id.item_local_music_add).getLayoutParams();
        params.width = params.height = size;
        params = holder.itemView.findViewById(R.id.item_local_music_more).getLayoutParams();
        params.width = params.height = size;
        holder.itemView.getLayoutParams().height =
                (int) (context.getResources().getDisplayMetrics().heightPixels * 1.0 / 11);
    }

    protected void changeChildViewState(LocalMusicHolder holder){
        if( musicList.get(holder.getAdapterPosition()).getFilePath().equals(
                ((NavBarActivity)context).getPlayingMusicPath()) ){
            //若是正在播放的歌曲则高亮
            holder.itemView.setBackgroundResource(R.drawable.layout_for_button_gray);
        }else{
            holder.itemView.setBackgroundResource( R.drawable.layout_for_button);
        }
    }

    @Override
    public int getItemCount() {
        return dbList.getCount();
    }

    protected ArrayList<Music> getMusicList() {
        return musicList;
    }

    interface OnItemClickListener {
        void onClick(LocalMusicAdapter adapter, LocalMusicHolder holder);
    }

}
