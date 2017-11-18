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
    private int maxFolderDepth;
    private int fileMinLength;

    public ScanServiceConnection(ArrayList<String> scanList,int maxFolderDepth,int fileMinLength){
        this.scanList = scanList;
        this.maxFolderDepth = maxFolderDepth;
        this.fileMinLength = fileMinLength;
    }

    public ScanServiceConnection(ArrayList<String> scanList){
        this.scanList = scanList;
        this.maxFolderDepth = 4;
        this.fileMinLength = 64;
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

    protected void beginScan(ArrayList<String> scanList,int maxFolderDepth, int fileMinLength){
        this.scanList = scanList;
        this.maxFolderDepth = 4;
        this.fileMinLength = 64;
        scanBinder.startScan(scanList,maxFolderDepth,fileMinLength);
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
