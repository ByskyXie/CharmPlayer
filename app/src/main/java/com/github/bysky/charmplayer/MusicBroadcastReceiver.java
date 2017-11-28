package com.github.bysky.charmplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class MusicBroadcastReceiver extends BroadcastReceiver {

    private BroadcastService service;
    //该类用于使播放服务接收控制指令
    MusicBroadcastReceiver(BroadcastService service){
        this.service = service;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        int data = intent.getIntExtra("OPERRATION",-1);
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
                if(list != null)
                    service.setBroadcastList(list);
                break;
        }
    }
}
