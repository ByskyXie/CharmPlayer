package com.github.bysky.charmplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BroadcastService extends Service implements Runnable {

    final static int PAUSE_MUSIC = 0;
    final static int PLAY_MUSIC = 1;
    final static int NEXT_MUSIC = 2;
    final static int PREVIOUS_MUSIC = 3;
    final static int CHANGE_PLAY_MODE = 4;
    final static int SET_BROADCAST_LIST = 5;
    final static int ADD_LIST_ITEM = 6;
    final static int CLEAR_BROADCAST_LIST = 7;

    final static int PLAY_MODE_ORDER = 100;
    final static int PLAY_MODE_REPEAT = 101;
    final static int PLAY_MODE_RANDOM = 102;

    private int playMode = PLAY_MODE_ORDER;
    private int playState = PAUSE_MUSIC;
    private MediaPlayer mediaPlayer;
    private ArrayList<String> broadcastList;
    private MusicBroadcastReceiver receiver = new MusicBroadcastReceiver(this);
    private IntentFilter filter;
    private int playPosition;   //用于记录当前播放歌曲的位置
    private Thread thread;
    //传入intent 的OPERATION部分相当于指令
    private Intent instruction;
    //用于控制界面上的按键
    private Handler handler;

    /**
     * MusicBroadcastReceiver类用于传输控制指令
     */
    static class MusicBroadcastReceiver extends BroadcastReceiver {

        private BroadcastService service;

        MusicBroadcastReceiver(BroadcastService service) {
            this.service = service;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            int data = intent.getIntExtra("OPERATION", -1);
            //含有指令则传递给播放线程
            Handler handler = (Handler)intent.getBundleExtra("Class").get("Handler");
            if (data != -1)
                service.instruction = intent;
        }
    }

    protected void setBroadcastList(ArrayList<String> pathList) {
        this.broadcastList = pathList;
    }

    protected void pause() {

    }

    protected void play() {

    }

    protected void playNextMusic() {

    }

    protected void playPreviousMusic() {

    }

    protected void setPlayMode(final int playMode) {

    }


    @Override
    public void run() {
        while (true) {
            //循环运行直到程序停止并退出 //TODO:会出现播放文件被删除的情况
            try {
                if (hasInstruction()) {
                    analyseInstruction();
                } else {
                    //根据列表及状态决定播放状态
                    autoExecute();
                }
            } catch (InterruptedException ie) {
                Log.e("BroadcastService.auto", ie.toString());
            } catch (IOException ioe) {
                Log.e("BroadcastService.auto", ioe.toString());
            }
        }
    }

    private void autoExecute() throws InterruptedException, IOException {
        if (broadcastList == null || broadcastList.isEmpty()) {
            thread.sleep(800);
            return;
        }
        if (playState == PLAY_MUSIC) {
            //允许播放
            if (!mediaPlayer.isPlaying()) {
                //持续播放已有列表，当停止时根据播放模式载入下一首
                switch (playMode) {
                    case PLAY_MODE_ORDER:
                        moveToNextPlayPostion();
                        playMusic(broadcastList.get(playPosition));
                        break;
                    case PLAY_MODE_REPEAT:
                        if (playPosition != broadcastList.size() - 1) {
                            playPosition++;
                            playMusic(broadcastList.get(playPosition));
                        } else
                            playState = PAUSE_MUSIC;
                        break;
                    case PLAY_MODE_RANDOM:
                        playPosition = (new Random().nextInt()) % broadcastList.size();
                        playMusic(broadcastList.get(playPosition));
                        break;
                }
            } else {
                //说明在播放中，内心毫无波动
            }
        } else {
            //说明状态为暂停
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();
            thread.sleep(800);
        }
    }

    /**
     * 用于判断是否有指令，当“OPERATION”不为空时代表有
     * */
    private boolean hasInstruction() {
        return (instruction != null && instruction.getIntExtra("OPERATION",-1)!=-1);
    }

    private void analyseInstruction() throws IOException {
        int operation = instruction.getIntExtra("OPERATION", -1);
        switch (operation) {
            case BroadcastService.PLAY_MUSIC:
                playState = PLAY_MUSIC;
                break;
            case BroadcastService.PAUSE_MUSIC:
                playState = PAUSE_MUSIC;
                break;
            case BroadcastService.NEXT_MUSIC:
                moveToNextPlayPostion();
                playMusic(broadcastList.get(playPosition));
                break;
            case BroadcastService.PREVIOUS_MUSIC:
                moveToPreviousPlayPostion();
                playMusic(broadcastList.get(playPosition));
                break;
            case BroadcastService.CHANGE_PLAY_MODE:
                //TODO:0x
                break;
            case BroadcastService.SET_BROADCAST_LIST:
                ArrayList<String> list = instruction.getStringArrayListExtra("BROADCAST_LIST");
                playPosition = instruction.getIntExtra("POSITION", 0);
                playState = PLAY_MUSIC;
                broadcastList = list;
                //播放歌曲;
                playMusic(broadcastList.get(playPosition));
                break;
            case ADD_LIST_ITEM:

                break;
            case CLEAR_BROADCAST_LIST:

                break;
        }
        instruction = null;
    }

    private void playMusic(String filePath) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void moveToNextPlayPostion() {
        if (broadcastList == null)
            return;
        if (playPosition + 1 == broadcastList.size())
            playPosition = 0;
        else
            playPosition++;
    }

    private void moveToPreviousPlayPostion() {
        if (broadcastList == null)
            return;
        if (playPosition == 0)
            playPosition = broadcastList.size() - 1;
        else
            playPosition--;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction("com.github.bysky.charmplayer.MUSIC_BROADCAST");
        }
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        registerReceiver(receiver, filter);
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
