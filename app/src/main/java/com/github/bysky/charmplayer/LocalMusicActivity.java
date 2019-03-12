package com.github.bysky.charmplayer;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class LocalMusicActivity extends NavBarActivity
        implements View.OnClickListener {

    private RecyclerView recyclerMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        Toolbar toolbar_local = findViewById(R.id.toolbar_local);
        setSupportActionBar(toolbar_local);
        //重写标题栏
        if(getIntent().getStringExtra("ARTIST") == null)
            getSupportActionBar().setTitle(R.string.local_title);
        else
            getSupportActionBar().setTitle(getIntent().getStringExtra("ARTIST"));
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialUI();
    }

    @Override
    protected void initialUI() {
        //recyclerView 的操作
        LocalMusicAdapter.OnItemClickListener listener = new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void onClick(LocalMusicAdapter adapter, LocalMusicAdapter.LocalMusicHolder holder) {
                Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
                intent.putExtra("OPERATION", BroadcastService.SET_BROADCAST_LIST);
                intent.putExtra("BROADCAST_LIST", adapter.getMusicList());
                intent.putExtra("POSITION", holder.getAdapterPosition());
                sendBroadcast(intent);
            }
        };
        //
        recyclerMusicList = findViewById(R.id.recycler_local_music);
        Cursor dbList = null;
        //如果传入了指定的歌手名，则只展出该歌手的歌曲
        if(getIntent().getStringExtra("ARTIST") != null){
            dbList = getSavedMusicList(getIntent().getStringExtra("ARTIST"));
        }else
            dbList = getSavedMusicList();
        LocalMusicAdapter adapter = new LocalMusicAdapter(this, dbList, listener);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        llm.setSmoothScrollbarEnabled(true);
        recyclerMusicList.setLayoutManager(llm);
        recyclerMusicList.setAdapter(adapter);
        recyclerMusicList.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }

    @Override
    public void onClick(View v) {
        //交给上层处理
        super.onClick(v);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        int downX = 0,downY = 0;
//        long time = System.currentTimeMillis();
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                downY = recyclerMusicList.getScrollY();
//                downX = recyclerMusicList.getScrollX();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                recyclerMusicList.setScrollY((int)(event.getY()-downY));
//                recyclerMusicList.setScrollX((int)(event.getX()-downX));
//                break;
//        }
        return super.onTouchEvent(event);
    }

}
