package com.github.bysky.charmplayer;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ScanFileForMusicActivity extends BaseActivity
    implements View.OnClickListener{

    private ScanServiceConnection connection;
    private Button button_cancel;
    private TextView textViewProgress;
    private ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_file_for_music);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_scan_file);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //初始化界面
        initialUI();
        //获得搜索目录
        Intent intent = getIntent();
        ArrayList<String> scanList = intent.getStringArrayListExtra("scanList");
        int maxFolderDepth = intent.getIntExtra("maxFoldDepth",4);
        int fileMinLength = intent.getIntExtra("fileMinLength",64);
        //TODO:需要和服务绑定，就涉及到多绑定互斥问题
        //启动全局搜索服务
        connection = new ScanServiceConnection(scanList,maxFolderDepth,fileMinLength);
        //绑定服务
        bindService(new Intent(this,ScanFileService.class),connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void initialUI() {
        int width = (int)(0.618*getResources().getDisplayMetrics().widthPixels);
        textViewProgress = (TextView)findViewById(R.id.text_view_scan_progress);
        imgView = (ImageView)findViewById(R.id.image_scanning_logo);
        button_cancel = (Button)findViewById(R.id.button_scan_cancel);
        button_cancel.setOnClickListener(this);
        //设置宽度
        imgView.getLayoutParams().width = imgView.getLayoutParams().height = width;
        button_cancel.getLayoutParams().width = width;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_scan_cancel:
                //TODO:后期涉及停止动画
                connection.stopScan();
                button_cancel.setBackgroundResource(R.drawable.layout_for_button);
                button_cancel.setTextColor(getResources().getColor(R.color.black));
                button_cancel.setText("已停止");
                button_cancel.setEnabled(false);
                Toast.makeText(this,"扫描已停止",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
