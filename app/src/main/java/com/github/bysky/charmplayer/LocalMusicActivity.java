package com.github.bysky.charmplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class LocalMusicActivity extends AppCompatActivity {
    private Toolbar toolbar_local;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);
        toolbar_local = (Toolbar) findViewById(R.id.toolbar_local);
        toolbar_local.setTitle(R.string.local_title);
        setSupportActionBar(toolbar_local);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
