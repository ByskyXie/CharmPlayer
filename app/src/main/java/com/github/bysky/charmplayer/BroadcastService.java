package com.github.bysky.charmplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.app.Notification.FLAG_FOREGROUND_SERVICE;
import static android.app.Notification.FLAG_ONGOING_EVENT;

public class BroadcastService extends BaseService implements Runnable {

    final static int STATE_NONE_MUSIC = 0;
    final static int STATE_PAUSE_MUSIC = 1;
    final static int STATE_PLAY_MUSIC = 2;
    //
    final static int NEXT_MUSIC = 10;
    final static int PREVIOUS_MUSIC = 11;
    final static int CHANGE_PLAY_MODE = 12;
    final static int GET_PLAY_STATE = 13;
    final static int SWITCH_MUSIC = 14; //专门为通知栏的控制键设置的值
    //
    final static int SET_BROADCAST_LIST = 100;
    final static int ADD_LIST_ITEM = 101;
    final static int CLEAR_BROADCAST_LIST = 102;
    final static int EXIT = 103;
    //
    final static int PLAY_MODE_ORDER = 1000;
    final static int PLAY_MODE_REPEAT = 1001;
    final static int PLAY_MODE_RANDOM = 1002;
    final static String NOTIFICATION = "NOTIFICATION_BROADCAST_SERVICE";
    final static int NOTI_CODE = 11235811;  //斐波那契..

    private int playMode = PLAY_MODE_ORDER;
    private int playState = STATE_NONE_MUSIC;
    private MediaPlayer mediaPlayer;
    private ArrayList<Music> broadcastList;
    private MusicBroadcastInstructionReceiver receiver = new MusicBroadcastInstructionReceiver(this);
    private IntentFilter filter;
    private Thread thread;
    private RemoteViews remoteViews;
    //用于记录当前播放歌曲在列表里的位置
    private int playPosition;
    //传入intent 的OPERATION部分相当于指令
    private Intent instruction;
    private NotificationCompat.Builder builder;

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
        remoteViews = new RemoteViews(getPackageName(),R.layout.notification_music);
        remoteViews.setTextViewText(R.id.music_title, getResources().getString(R.string.default_music_title));
        remoteViews.setTextViewText(R.id.music_artist, getResources().getString(R.string.default_music_art));
        remoteViews.setImageViewResource(R.id.img_button_play,R.drawable.ic_notification_play);
        remoteViews.setImageViewResource(R.id.img_button_pre,R.drawable.ic_notification_previous);
        remoteViews.setImageViewResource(R.id.img_button_next,R.drawable.ic_notification_next);
        remoteViews.setImageViewResource(R.id.img_button_exit,R.drawable.ic_cancel);
        //设置点击事件
        setRemoteClickEvent(remoteViews);
        //通知栏
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pint = PendingIntent.getActivities(this,0,
                new Intent[]{intent},PendingIntent.FLAG_CANCEL_CURRENT);
        //准备通知
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(builder == null){
            builder = new NotificationCompat.Builder(BroadcastService.this, NOTIFICATION)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())    //自定义视图
                    .setCustomContentView(remoteViews)  //布局
                    .setContentIntent(pint) //响应事件
                    .setSmallIcon(R.mipmap.charm_small)   //不需要小图标
                    .setOngoing(true)   //设为常驻通知栏
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.charm))   //图标
                    .setShowWhen(false);
        }
        if(notiManager != null)
            notiManager.notify(NOTIFICATION, NOTI_CODE, builder.build());
        //正式响应
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

    private void setRemoteClickEvent(RemoteViews remoteViews){
        Intent intentPre = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
        intentPre.putExtra("OPERATION",BroadcastService.PREVIOUS_MUSIC);
        remoteViews.setOnClickPendingIntent(R.id.img_button_pre,PendingIntent
                .getBroadcast(this,BroadcastService.PREVIOUS_MUSIC, intentPre,PendingIntent.FLAG_CANCEL_CURRENT));

        Intent intentNext = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
        intentNext.putExtra("OPERATION",BroadcastService.NEXT_MUSIC);
        remoteViews.setOnClickPendingIntent(R.id.img_button_next,PendingIntent
                .getBroadcast(this,BroadcastService.NEXT_MUSIC, intentNext,PendingIntent.FLAG_CANCEL_CURRENT));

        Intent intentPlay = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
        intentPlay.putExtra("OPERATION",BroadcastService.SWITCH_MUSIC);
        remoteViews.setOnClickPendingIntent(R.id.img_button_play,PendingIntent
                .getBroadcast(this,BroadcastService.SWITCH_MUSIC, intentPlay,PendingIntent.FLAG_CANCEL_CURRENT));

        Intent intentCancel = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
        intentCancel.putExtra("OPERATION",BroadcastService.EXIT);
        remoteViews.setOnClickPendingIntent(R.id.img_button_exit,PendingIntent
                .getBroadcast(this,BroadcastService.EXIT, intentCancel,PendingIntent.FLAG_CANCEL_CURRENT));
    }

    private void autoExecute() throws InterruptedException, IOException {
        if (broadcastList == null || broadcastList.isEmpty()) {
            thread.sleep(800);
            return;
        }
        if (playState == STATE_PLAY_MUSIC) {
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
        } else if (playState == STATE_PAUSE_MUSIC) {
            //设置状态为暂停
            if (mediaPlayer.isPlaying())
                pauseMusic();
        } else if (playState == STATE_NONE_MUSIC) {
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
            case BroadcastService.STATE_PLAY_MUSIC:
                //发送信息的事，该方法内部做了
                playMusic();
                break;

            case BroadcastService.STATE_PAUSE_MUSIC:
                pauseMusic();
                break;

            case BroadcastService.NEXT_MUSIC:
                if(broadcastList == null)
                    break;
                moveToNextPlayPosition();
                playMusic(broadcastList.get(playPosition));
                intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
                intent.putExtra("PLAY_STATE",playState);
                intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_NEXT);
                sendBroadcast(intent);
                break;

            case BroadcastService.PREVIOUS_MUSIC:
                if(broadcastList == null)
                    break;
                moveToPreviousPlayPosition();
                playMusic(broadcastList.get(playPosition));
                intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
                intent.putExtra("PLAY_STATE",playState);
                intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PRE);
                sendBroadcast(intent);
                break;

            case BroadcastService.SWITCH_MUSIC:
                //当按下通知栏的转换按钮
                if(playState == STATE_PLAY_MUSIC)
                    pauseMusic();
                else if(playState == STATE_PAUSE_MUSIC)
                    playMusic();
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
                //传入歌名
                if(broadcastList != null){
                    intent.putExtra("TITLE",broadcastList.get(playPosition).getMusicName());
                    intent.putExtra("ARTIST",broadcastList.get(playPosition).getArtist());
                    intent.putExtra("MUSIC",broadcastList.get(playPosition));
                }
                sendBroadcast(intent);
                break;

            case EXIT:
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTI_CODE);
                System.exit(0);
        }
        //用完的指令还有什么用，烧了(╯≥▽≤)╯~ ┴—┴
        instruction = null;
    }



    private void playMusic() {
        if (playState != STATE_PAUSE_MUSIC)
            return;
        mediaPlayer.start();
        playState = STATE_PLAY_MUSIC;
        //更新通知视图
        if(remoteViews != null){
            remoteViews.setTextViewText(R.id.music_title, broadcastList.get(playPosition).getMusicName());
            remoteViews.setTextViewText(R.id.music_artist, broadcastList.get(playPosition).getArtist());
            remoteViews.setImageViewResource(R.id.img_button_play,R.drawable.ic_notification_pause);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION, NOTI_CODE, builder.setContent(remoteViews).build());
        }
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        intent.putExtra("TITLE",broadcastList.get(playPosition).getMusicName());
        intent.putExtra("ARTIST",broadcastList.get(playPosition).getArtist());
        sendBroadcast(intent);
    }

    private void playMusic(Music music) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(music.getFilePath());
        mediaPlayer.prepare();
        mediaPlayer.start();
        playState = STATE_PLAY_MUSIC;
        //更新通知视图
        if(remoteViews != null){
            remoteViews.setTextViewText(R.id.music_title, broadcastList.get(playPosition).getMusicName());
            remoteViews.setTextViewText(R.id.music_artist, broadcastList.get(playPosition).getArtist());
            remoteViews.setImageViewResource(R.id.img_button_play,R.drawable.ic_notification_pause);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION, NOTI_CODE, builder.setContent(remoteViews).build());
        }
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        //改变
        intent.putExtra("TITLE",broadcastList.get(playPosition).getMusicName());
        intent.putExtra("ARTIST",broadcastList.get(playPosition).getArtist());
        sendBroadcast(intent);
    }

    private void pauseMusic() {
        if (!mediaPlayer.isPlaying())
            return;
        mediaPlayer.pause();
        playState = STATE_PAUSE_MUSIC;
        //更新通知视图
        if(remoteViews != null){
            remoteViews.setTextViewText(R.id.music_title, broadcastList.get(playPosition).getMusicName());
            remoteViews.setTextViewText(R.id.music_artist, broadcastList.get(playPosition).getArtist());
            remoteViews.setImageViewResource(R.id.img_button_play,R.drawable.ic_notification_play);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION, NOTI_CODE, builder.setContent(remoteViews).build());
        }
        //communication
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        intent.putExtra("PLAY_STATE",playState);
        intent.putExtra("TARGET_VIEW",NavBarActivity.BUTTON_PLAY);
        sendBroadcast(intent);
    }

    private void resetMusic() {
        mediaPlayer.reset();
        playState = STATE_NONE_MUSIC;
        //更新通知视图
        if(remoteViews != null){
            remoteViews.setTextViewText(R.id.music_title, getResources().getString(R.string.default_music_title));
            remoteViews.setTextViewText(R.id.music_artist, getResources().getString(R.string.default_music_art));
            remoteViews.setImageViewResource(R.id.img_button_play,R.drawable.ic_notification_play);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(NOTIFICATION, NOTI_CODE, builder.setContent(remoteViews).build());
        }
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
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(NOTI_CODE);
    }

}
