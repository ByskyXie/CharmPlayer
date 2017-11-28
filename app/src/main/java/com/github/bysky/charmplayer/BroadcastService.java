package com.github.bysky.charmplayer;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

public class BroadcastService extends Service implements Runnable{

    final static int PAUSE_MUSIC = 0;
    final static int PLAY_MUSIC = 1;
    final static int NEXT_MUSIC = 2;
    final static int PREVIOUS_MUSIC = 3;
    final static int CHANGE_PLAY_MODE = 4;
    final static int SET_BROADCAST_LIST = 5;

    final static int PLAY_MODE_ORDER = 100;
    final static int PLAY_MODE_REPEAT = 101;
    final static int PLAY_MODE_RANDOM = 102;

    private int playMode = PLAY_MODE_ORDER;
    private ArrayList<String> broadcastList;
    private MusicBroadcastReceiver receiver = new MusicBroadcastReceiver(this);
    private Thread thread;

    protected void setBroadcastList(ArrayList<String> pathList){
        this.broadcastList = pathList;
    }

    protected void pause(){

    }

    protected void play(){

    }

    protected void playNextMusic(){

    }

    protected void playPreviousMusic(){

    }

    protected void setPlayMode(final int playMode){

    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void run() {
        while(true){
            //循环运行直到程序停止并退出 //会出现播放文件被删除的情况
            if(broadcastList==null || broadcastList.isEmpty()){
                try{
                    thread.sleep(500);
                }catch (InterruptedException ie){
                    Log.e(".BroadcastService","ERR\n"+ie);
                }
                continue;
            }

        }
    }
}
