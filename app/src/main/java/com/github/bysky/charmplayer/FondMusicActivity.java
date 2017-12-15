package com.github.bysky.charmplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class FondMusicActivity extends NavBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fond_music);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_fond));
        getSupportActionBar().setTitle(getResources().getString(R.string.music_fond));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialUI();
        //recycler变化
        RecyclerView recyclerView = findViewById(R.id.recycler_fond);
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
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        llm.setSmoothScrollbarEnabled(true);
        LocalMusicAdapter adapter = new LocalMusicAdapter(this,getFondList(),listener);
        recyclerView.setVerticalScrollBarEnabled(true); //滚动条
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initialUI() {

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }
}
