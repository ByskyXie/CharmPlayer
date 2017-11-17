package com.github.bysky.charmplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ScanMusicActivity extends BaseActivity
    implements View.OnClickListener{

    private Button button_scan_all,button_scan_diy;
    private ImageView img_view;
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
        //初始化控件
        initialUI();
    }

    @Override
    protected void initialUI() {
        //以下设置logo、按钮尺寸
        int width =  (int)(0.618*getResources().getDisplayMetrics().widthPixels);
        img_view = (ImageView)findViewById(R.id.img_scan_music_logo);
        ViewGroup.LayoutParams params = img_view.getLayoutParams();
        params.width = params.height = width;   //黄金比例
        button_scan_all = (Button)findViewById(R.id.button_scan_all);   button_scan_all.setOnClickListener(this);
        button_scan_all.getLayoutParams().width =  width;
        button_scan_diy = (Button)findViewById(R.id.button_scan_diy);   button_scan_diy.setOnClickListener(this);
        button_scan_diy.getLayoutParams().width =  width;
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
