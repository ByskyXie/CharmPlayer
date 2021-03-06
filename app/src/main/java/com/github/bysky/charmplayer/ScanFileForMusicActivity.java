package com.github.bysky.charmplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
    private Button buttonCancel;
    private TextView textViewProgress;
    private ImageView imgView;
    private ScanHandler handler;

    public static class ScanHandler extends Handler{
        final static int SCAN_FINISH = 1;
        final static int SCAN_STOP = 0;
        private Context context;
        private Button controlButton;

        ScanHandler(Context context,Button controlButton){
            this.context = context;
            this.controlButton = controlButton;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCAN_FINISH:
                    ((BaseActivity)context).setButtonUnable(controlButton);
                    Toast.makeText(context.getApplicationContext(),"扫描完成",Toast.LENGTH_SHORT).show();
                    break;
                case SCAN_STOP:
                    ((BaseActivity)context).setButtonUnable(controlButton);
                    controlButton.setText("已停止");
                    Toast.makeText(context.getApplicationContext(),"扫描已停止",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_file_for_music);
        Toolbar toolbar = findViewById(R.id.toolbar_scan_file);
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
        connection = new ScanServiceConnection(scanList,handler,maxFolderDepth,fileMinLength);
        //绑定服务
        bindService(new Intent(this,ScanFileService.class),connection,BIND_AUTO_CREATE);
    }

    @Override
    protected void initialUI() {
        int iconSize = (int)(0.618*getResources().getDisplayMetrics().widthPixels);
        textViewProgress = findViewById(R.id.text_view_scan_progress);
        imgView = findViewById(R.id.image_scanning_logo);
        buttonCancel = findViewById(R.id.button_scan_cancel);
        buttonCancel.setOnClickListener(this);
        //设置宽度
        imgView.getLayoutParams().width = imgView.getLayoutParams().height = iconSize;
        buttonCancel.getLayoutParams().width = iconSize;
        handler = new ScanHandler(this,buttonCancel);
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
                break;
        }
    }

    public Button getButtonCancel() {
        return buttonCancel;
    }
}
