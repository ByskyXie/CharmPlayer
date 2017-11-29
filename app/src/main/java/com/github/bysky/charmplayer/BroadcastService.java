package com.github.bysky.charmplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
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
    private int playState = PAUSE_MUSIC;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> broadcastList;
    private MusicBroadcastReceiver receiver = new MusicBroadcastReceiver(this);
    private IntentFilter filter;
    private int playPosition;
    private Thread thread;

    /**
     * MusicBroadcastReceiver类用于使播放服务接收控制指令
     */
    static class MusicBroadcastReceiver extends BroadcastReceiver {

        private BroadcastService service;
        MusicBroadcastReceiver(BroadcastService service){
            this.service = service;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            int data = intent.getIntExtra("OPERATION",-1);
            switch (data){
                case BroadcastService.PLAY_MUSIC:

                    break;
                case BroadcastService.PAUSE_MUSIC:

                    break;
                case BroadcastService.NEXT_MUSIC:

                    break;
                case BroadcastService.PREVIOUS_MUSIC:

                    break;
                case BroadcastService.CHANGE_PLAY_MODE:

                    break;
                case BroadcastService.SET_BROADCAST_LIST:
                    ArrayList<String> list = intent.getStringArrayListExtra("BROADCAST_LIST");
                    service.playPosition = intent.getIntExtra("POSITION",0);
                    if(!list.isEmpty())
                        service.setBroadcastList(list);
                    break;
            }
        }
    }

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
    public void run() {
        playPosition = 0;
        while(true){
            //循环运行直到程序停止并退出 //会出现播放文件被删除的情况
            try{
                if(broadcastList == null || broadcastList.isEmpty() || playState == PAUSE_MUSIC){
                    //未播放歌曲时
                    thread.sleep(800);
                    continue;
                }
                if(mediaPlayer.isPlaying()){
                    //播放过程中的操作
                    if(playState == PAUSE_MUSIC){
                        mediaPlayer.stop();
                    }


                }
                mediaPlayer.setDataSource(broadcastList.get(playPosition));
                //下一首位置
                if(playPosition == broadcastList.size()-1)
                    playPosition++;
                else
                    playPosition = 0;
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (IOException ioe){
                Log.e(".BroadcastService",ioe.toString());
            }
            catch (InterruptedException ie){
                Log.e(".BroadcastService","ERR\n"+ie);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(filter == null){
            filter = new IntentFilter();
            filter.addAction("com.github.bysky.charmplayer.MUSIC_BROADCAST");
        }
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        registerReceiver(receiver,filter);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
