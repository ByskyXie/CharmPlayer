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
    private ArrayList<String> musicList = new ArrayList<String>();

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
        if(dbList.moveToFirst()){
            do{
                musicList.add(dbList.getString(dbList.getColumnIndex("FILE_PATH")));
            }while (dbList.moveToNext());
        }
    }

    @Override
    public LocalMusicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_music,parent,false);
        return new LocalMusicHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocalMusicHolder holder, final int position) {
        if(!dbList.moveToPosition(holder.getAdapterPosition())){
            Log.e("MusicAdapter","错误的列表位置");
            return;
        }
        String fileName = dbList.getString(dbList.getColumnIndex("FILE_NAME"));
        //通过正则检测是否符合【歌手 - 歌曲名】规范
        if(fileName.matches(".+[ ]+[-]{1}[ ]+.+")){
            int temp = fileName.indexOf('-');
            holder.textViewMusicArtist.setText(fileName.substring(0,temp));
            //去除多余空格
            while(fileName.charAt(temp+1)==' ')
                temp++;
            holder.textViewMusicName.setText(fileName.substring(temp+1));
        }else{
            holder.textViewMusicName.setText(fileName);
            holder.textViewMusicArtist.setText("未知歌手");
        }
        //监听器
        holder.layoutPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(LocalMusicAdapter.this,holder.getAdapterPosition());
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

    protected ArrayList<String> getMusicList(){
        return musicList;
    }

    interface OnItemClickListener{
        void onClick(LocalMusicAdapter adapter,int position);
    }

}
