package com.github.bysky.charmplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class BroadcastService extends Service implements Runnable {

    final static int NONE_MUSIC = 0;
    final static int PAUSE_MUSIC = 1;
    final static int PLAY_MUSIC = 2;
    //
    final static int NEXT_MUSIC = 10;
    final static int PREVIOUS_MUSIC = 11;
    final static int CHANGE_PLAY_MODE = 12;
    final static int GET_PLAY_STATE = 13;
    //
    final static int SET_BROADCAST_LIST = 100;
    final static int ADD_LIST_ITEM = 101;
    final static int CLEAR_BROADCAST_LIST = 102;
    //
    final static int PLAY_MODE_ORDER = 1000;
    final static int PLAY_MODE_REPEAT = 1001;
    final static int PLAY_MODE_RANDOM = 1002;

    private int playMode = PLAY_MODE_ORDER;
    private int playState = NONE_MUSIC;
    private MediaPlayer mediaPlayer;
    private ArrayList<Music> broadcastList;
    private MusicBroadcastInstructionReceiver receiver = new MusicBroadcastInstructionReceiver(this);
    private IntentFilter filter;
    private Thread thread;
    //用于记录当前播放歌曲在列表里的位置
    private int playPosition;
    //传入intent 的OPERATION部分相当于指令
    private Intent instruction;

    /**
     * MusicBroadcastReceiver类用于传输控制指令
     */
    static class MusicBroadcastInstructionReceiver extends BroadcastReceiver {

        private BroadcastService service;

        MusicBroadcastInstructionReceiver(BroadcastService service) {
            this.service = service;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO: This method is called when the BroadcastReceiver is receiving
            //含有指令则传递给播放线程
            if (intent.getIntExtra("OPERATION", -1) != -1)
                service.instruction = intent;
        }
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
                //情况一：已有歌曲，但暂停中

                //情况二：持续播放已有列表，当停止时根据播放模式载入下一首
                switch (playMode) {
                    case PLAY_MODE_ORDER:
                        moveToNextPlayPosition();
                        playMusic(broadcastList.get(playPosition));
                        break;

                    case PLAY_MODE_REPEAT:
                        if (playPosition != broadcastList.size() - 1) {
                            playPosition++;
                            playMusic(broadcastList.get(playPosition));
                        } else {
                            playPosition = 0;
                            resetMusic();
                        }
                        break;

                    case PLAY_MODE_RANDOM:
                        playPosition = (new Random().nextInt()) % broadcastList.size();
                        playMusic(broadcastList.get(playPosition));
                        break;
                }
            } else {
                //说明在播放中，内心毫无波动
                thread.sleep(800);
            }
        } else if (playState == PAUSE_MUSIC) {
            //设置状态为暂停
            if (mediaPlayer.isPlaying())
                pauseMusic();
        } else if (playState == NONE_MUSIC) {
            resetMusic();
        }
    }

    /**
     * 用于判断是否有指令，当“OPERATION”不为空时代表有
     */
    private boolean hasInstruction() {
        return (instruction != null && instruction.getIntExtra("OPERATION", -1) != -1);
    }

    private void analyseInstruction() throws IOException {
        int operation = instruction.getIntExtra("OPERATION", -1);
        Intent intent;
        switch (operation) {
            case BroadcastService.PLAY_MUSIC:
                //发送信息的事，该方法内部做了
                playMusic();
                break;

            case BroadcastService.PAUSE_MUSIC:
                pauseMusic();
                break;

            case BroadcastService.NEXT_MUSIC:
                moveToNextPlayPosition();
                playMusic(broadcastList.get(playPosition));
                intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
                intent.putExtra("PLAY_STATE",playState);
                intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_NEXT);
                sendBroadcast(intent);
                break;

            case BroadcastService.PREVIOUS_MUSIC:
                moveToPreviousPlayPosition();
                playMusic(broadcastList.get(playPosition));
                intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
                intent.putExtra("PLAY_STATE",playState);
                intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PRE);
                sendBroadcast(intent);
                break;

            case BroadcastService.CHANGE_PLAY_MODE:
                //TODO:0x
                break;

            case BroadcastService.SET_BROADCAST_LIST:
                ArrayList<Music> list = (ArrayList<Music>) instruction.getSerializableExtra("BROADCAST_LIST");
                playPosition = instruction.getIntExtra("POSITION", 0);
                broadcastList = list;
                //发送信息的事，该方法内部做了
                //播放歌曲;
                playMusic(broadcastList.get(playPosition));
                break;

            case ADD_LIST_ITEM:

                break;
            case CLEAR_BROADCAST_LIST:

                break;
            case GET_PLAY_STATE:
                intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
                intent.putExtra("PLAY_STATE",playState);
                intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
                sendBroadcast(intent);
                break;
        }
        //用完的指令还有什么用，烧了(╯≥▽≤)╯~ ┴—┴
        instruction = null;
    }

    private void playMusic() {
        if (playState != PAUSE_MUSIC)
            return;
        mediaPlayer.start();
        playState = PLAY_MUSIC;
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        sendBroadcast(intent);
    }

    private void playMusic(Music music) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(music.getFilePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        playState = PLAY_MUSIC;
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        //改变
        String fileName = music.getFileName();
        if (fileName.matches(".+[ ]+[-]{1}[ ]+.+")) {
            int temp = fileName.indexOf('-');
            while (fileName.charAt(temp + 1) == ' ')
                temp++;
            intent.putExtra("TITLE",fileName.substring(temp+1));
        } else {
            intent.putExtra("TITLE",fileName);
        }
        sendBroadcast(intent);
    }

    private void pauseMusic() {
        if (!mediaPlayer.isPlaying())
            return;
        mediaPlayer.pause();
        playState = PAUSE_MUSIC;
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        sendBroadcast(intent);
    }

    private void resetMusic() {
        mediaPlayer.reset();
        playState = NONE_MUSIC;
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        sendBroadcast(intent);
    }

    private void moveToNextPlayPosition() {
        if (broadcastList == null)
            return;
        if (playPosition + 1 == broadcastList.size())
            playPosition = 0;
        else
            playPosition++;
    }

    private void moveToPreviousPlayPosition() {
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
            filter.addAction("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
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
