package com.github.bysky.charmplayer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

public class ArtistActivity extends NavBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_artist));
        getSupportActionBar().setTitle(R.string.artist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialUI();
        RecyclerView recyclerView = findViewById(R.id.recycler_artist);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        llm.setSmoothScrollbarEnabled(true);
        ArtistAdapter adapter = new ArtistAdapter(this,getArtistList());
        recyclerView.setVerticalScrollBarEnabled(true); //滚动条
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initialUI() {

    }
}
