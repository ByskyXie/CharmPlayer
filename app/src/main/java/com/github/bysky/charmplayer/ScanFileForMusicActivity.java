package com.github.bysky.charmplayer;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ScanFileForMusicActivity extends BaseActivity
    implements View.OnClickListener{

    private Button button_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_file_for_music);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_scan_file);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_cancel = (Button)findViewById(R.id.button_scan_cancel);
        button_cancel.setOnClickListener(this);
        //启动全局搜索服务

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_scan_cancel:
                //TODO:停止服务
                break;
        }
    }
}
