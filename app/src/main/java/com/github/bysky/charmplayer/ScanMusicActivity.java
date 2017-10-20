package com.github.bysky.charmplayer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ScanMusicActivity extends AppCompatActivity {

    private Toolbar toolbar_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music);
        toolbar_scan = (Toolbar) findViewById(R.id.toolbar_scan);
        toolbar_scan.setTitle(R.string.scan_title);
        setSupportActionBar(toolbar_scan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
