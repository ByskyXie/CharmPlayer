package com.github.bysky.charmplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by asus on 2017/12/1.
 */

public class NavBarActivity extends BaseActivity {

    final static int BUTTON_PLAY = 1;
    final static int BUTTON_NEXT = 2;
    final static int BUTTON_PRE = 3;
    final static int PROGRESS_BAR = 4;

    private Button navButtonPlay;
    private Button navButtonPre;
    private Button navButtonNext;
    private ProgressBar navProgressBar;
    private int playState;
    protected MusicBroadcastStateReceiver stateReceiver;

    public class MusicBroadcastStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            playState = intent.getIntExtra("PLAY_STATE",BroadcastService.NONE_MUSIC);
            int target = intent.getIntExtra("TARGET_VIEW",-1);
            switch (target){
                case BUTTON_PLAY:
                    navButtonPlay.setEnabled(true);
                    if(playState == BroadcastService.PLAY_MUSIC){
                        navButtonPlay.setBackgroundResource(R.drawable.ic_pause);
                    }else{
                        navButtonPlay.setBackgroundResource(R.drawable.ic_play);
                    }
                    break;
                case BUTTON_PRE:

                    break;
                case BUTTON_NEXT:

                    break;
                case PROGRESS_BAR:

                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //此时已得到view
        navButtonPlay = findViewById(R.id.button_control_play);
        navButtonPre = findViewById(R.id.button_control_pre);
        navButtonNext = findViewById(R.id.button_control_next);
        stateReceiver = new MusicBroadcastStateReceiver();
        IntentFilter filter = new IntentFilter("com.github.bysky.charmplayer.MUSIC_BROADCAST_STATE");
        registerReceiver(stateReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(stateReceiver);
    }

    public int getPlayState(){
        return playState;
    }
}
