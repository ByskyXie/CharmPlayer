package com.github.bysky.charmplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by asus on 2017/12/1.
 */

public class NavBarActivity extends BaseActivity {

    final static int BUTTON_PLAY = 1;
    final static int BUTTON_NEXT = 2;
    final static int BUTTON_PRE = 3;
    final static int TEXT_TITLE = 4;
    final static int TEXT_LRC = 5;
    final static int PROGRESS_BAR = 6;

    private Button navButtonPlay;
    private Button navButtonPre;
    private Button navButtonNext;
    private TextView navTextTitle;
    private TextView navTextArt;
    private ProgressBar navProgressBar;
    private int playState;
    private Music music;    //当前播放歌曲
    protected MusicBroadcastStateReceiver stateReceiver;

    public class MusicBroadcastStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            playState = intent.getIntExtra("PLAY_STATE",BroadcastService.NONE_MUSIC);
            int target = intent.getIntExtra("TARGET_VIEW",-1);
            switch (target){
                case BUTTON_PLAY:
                    if(playState != BroadcastService.NONE_MUSIC)
                        navButtonPlay.setEnabled(true);
                    else
                        navButtonPlay.setEnabled(false);
                    //设置图标
                    if(playState == BroadcastService.PLAY_MUSIC)
                        navButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                    else
                        navButtonPlay.setBackgroundResource(R.drawable.ic_play);
                    //歌曲名及歌词
                    if(intent.getStringExtra("TITLE") != null)
                        navTextTitle.setText(intent.getStringExtra("TITLE") );
                    if(intent.getStringExtra("ARTIST") != null)
                        navTextArt.setText(intent.getStringExtra("ARTIST") );
                    if(intent.getSerializableExtra("MUSIC") !=null)
                        music = (Music)intent.getSerializableExtra("MUSIC");
                    break;
                case BUTTON_PRE:
                    navButtonPre.setEnabled(true);
                    break;
                case BUTTON_NEXT:
                    navButtonNext.setEnabled(true);
                    break;
                case TEXT_TITLE:

                    break;
                case TEXT_LRC:

                    break;
                case PROGRESS_BAR:
                    //重写Drawable中的setLevel方法
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            music = (Music)savedInstanceState.getSerializable("MUSIC");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //此时已得到view
        //恢复数据
        navTextTitle = findViewById(R.id.text_view_music_name);
        navTextArt = findViewById(R.id.text_view_music_art);
        if(music != null){
            String[] strings = getArtistAndMusic(music);
            navTextTitle.setText(strings[1]);
            navTextArt.setText(strings[0]);
        }
        navButtonPlay = findViewById(R.id.button_control_play);
        navButtonPre = findViewById(R.id.button_control_pre);
        navButtonNext = findViewById(R.id.button_control_next);
        if(stateReceiver == null){
            stateReceiver = new MusicBroadcastStateReceiver();
            IntentFilter filter = new IntentFilter("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
            registerReceiver(stateReceiver,filter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent("com.github.bysky.charmplayer.MUSIC_BROADCAST_INSTRUCTION");
        intent.putExtra("OPERATION",BroadcastService.GET_PLAY_STATE);
        sendBroadcast(intent);
        //TODO:有列表的活动中，获取歌曲信息并高亮
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(music != null)
            outState.putSerializable("MUSIC",music);
    }

    public int getPlayState(){
        return playState;
    }
}
