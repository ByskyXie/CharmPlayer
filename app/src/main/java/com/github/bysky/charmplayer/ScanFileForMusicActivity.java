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
import java.io.File;
import java.util.ArrayList;

public class ScanFileForMusicActivity extends BaseActivity
    implements View.OnClickListener{

    private ScanServiceConnection connection;
    private Button button_cancel;

    private class ScanServiceConnection implements ServiceConnection{
        public ScanFileService.ScanBinder scanBinder;
        public ArrayList<String> list;
        public ScanServiceConnection(ArrayList<String> list){ this.list=list;}
        public void beginScan(){
            if(scanBinder!=null)
                scanBinder.startScan(list);
            else
                Log.e(".ScanFileForMusicAct","未调用onBind()未接受到Binder");
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            scanBinder = (ScanFileService.ScanBinder)service;
            beginScan();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //TODO:当解绑时要做的事

        }
    }

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
        Intent intent = new Intent(this,ScanFileService.class);
        //TODO:需要和服务绑定，就涉及到多绑定互斥问题
        connection = new ScanServiceConnection(getSelectedFolder());
        //绑定服务
        bindService(intent,connection,BIND_AUTO_CREATE);
    }

    public ArrayList<String> getSelectedFolder(){
        ArrayList<String> arry = new ArrayList<String>();
        File[] fileTree = Environment.getExternalStorageDirectory().listFiles();
        for(File file:fileTree)
            arry.add(file.getAbsolutePath());
        return arry;
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
                //TODO:停止服务 stopService( )
                break;
        }
    }
}
