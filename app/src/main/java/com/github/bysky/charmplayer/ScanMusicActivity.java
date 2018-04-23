package com.github.bysky.charmplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

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
        //检查权限
        if(ContextCompat.checkSelfPermission(ScanMusicActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //申请权限
            ActivityCompat.requestPermissions(ScanMusicActivity.this
                    ,new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},9527);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 9527:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                    //不同意，还怎么玩，回家
                    setButtonUnable(button_scan_all);
                    setButtonUnable(button_scan_diy);
                    Toast.makeText(ScanMusicActivity.this,"无权限访问存储卡",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch(v.getId()){
            case R.id.button_scan_all:
                intent = new Intent(this,ScanFileForMusicActivity.class);
                intent.putStringArrayListExtra("scanList",getExternalFolder());
                startActivity(intent);
                break;
            case R.id.button_scan_diy:
                intent = new Intent(this,SelectFolderActivity.class);
                startActivity(intent);
                break;
        }
    }
    //用于获取默认扫描文件夹
    public ArrayList<String> getExternalFolder(){
        ArrayList<String> arry = new ArrayList<String>();
        File[] fileTree = Environment.getExternalStorageDirectory().listFiles();
        for(File file:fileTree)
            arry.add(file.getAbsolutePath());
        return arry;
    }
}
