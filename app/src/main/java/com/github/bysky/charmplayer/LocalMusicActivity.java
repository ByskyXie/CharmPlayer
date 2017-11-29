package com.github.bysky.charmplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

public class LocalMusicActivity extends BaseActivity {
    private Toolbar toolbar_local;
    private RecyclerView recyclerMusicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        toolbar_local = (Toolbar) findViewById(R.id.toolbar_local);
        toolbar_local.setTitle(R.string.local_title);
        setSupportActionBar(toolbar_local);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialUI();
    }

    @Override
    protected void initialUI() {
        LocalMusicAdapter.OnItemClickListener listener = new LocalMusicAdapter.OnItemClickListener() {
            @Override
            public void onClick(LocalMusicAdapter adapter, int position) {
                Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST");
                intent.putExtra("OPERATION", BroadcastService.SET_BROADCAST_LIST);
                intent.putExtra("BROADCAST_LIST",adapter.getMusicList());
                intent.putExtra("POSITION",position);
                sendBroadcast(intent);
            }
        };
        //
        recyclerMusicList = findViewById(R.id.recycler_local_music);
        LocalMusicAdapter adapter = new LocalMusicAdapter(this,getSavedMusicList(),listener);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayout.VERTICAL,false);
        recyclerMusicList.setLayoutManager(llm);
        recyclerMusicList.setAdapter(adapter);
        recyclerMusicList.addItemDecoration(new DividerItemDecoration(this,LinearLayout.VERTICAL));
    }

}
