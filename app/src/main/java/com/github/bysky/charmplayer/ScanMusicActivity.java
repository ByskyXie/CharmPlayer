package com.github.bysky.charmplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ScanMusicActivity extends AppCompatActivity
    implements View.OnClickListener{

    private Button button_scan_all,button_scan_diy;
    private Toolbar toolbar_scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_music);
        toolbar_scan = (Toolbar) findViewById(R.id.toolbar_scan);
        toolbar_scan.setTitle(R.string.scan_title);
        setSupportActionBar(toolbar_scan);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_scan_all = (Button)findViewById(R.id.button_scan_all);   button_scan_all.setOnClickListener(this);
        button_scan_diy = (Button)findViewById(R.id.button_scan_diy);   button_scan_diy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_scan_all:
                intent = new Intent(this,ScanFileForMusicActivity.class);
                startActivity(intent);
                break;
            case R.id.button_scan_diy:
                intent = new Intent(this,SelectFolderActivity.class);
                startActivity(intent);
                break;
        }
    }
}
