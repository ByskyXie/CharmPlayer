package com.github.bysky.charmplayer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by asus on 2017/11/17.
 */

public class ScanServiceConnection implements ServiceConnection {

    private ScanFileService.ScanBinder scanBinder;
    private ArrayList<String> scanList;
    private int maxFolderDepth = 4;
    private int fileMinLength = 64;

    public ScanServiceConnection(ArrayList<String> scanList){
        this.scanList = scanList;
    }

    public void setScanList(ArrayList<String> scanList){
        this.scanList = scanList;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        scanBinder = (ScanFileService.ScanBinder)service;
        beginScan();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //TODO:Android 系统会在与服务的连接意外中断时（例如当服务崩溃或被终止时）调用该方法 注意:当客户端取消绑定时，系统“绝对不会”调用该方法

    }
    protected void beginScan(){
        if(scanBinder!=null)
            scanBinder.startScan(scanList,maxFolderDepth,fileMinLength);
        else
            Log.e(".ScanFileForMusicAct","未调用onBind()未接受到Binder");
    }

    protected void beginScan(ArrayList<String> scanList){
        this.scanList = scanList;
        beginScan();
    }

    protected void stopScan(){
        scanBinder.stopScan();
    }

    protected ScanFileService.ScanBinder getScanBinder(){
        return scanBinder;
    }

}
