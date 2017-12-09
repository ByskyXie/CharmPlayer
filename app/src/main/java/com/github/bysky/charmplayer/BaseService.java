package com.github.bysky.charmplayer;

import android.app.Service;

/**
 * Created by asus on 2017/12/9.
 */

public abstract class BaseService extends Service {

    //用于匹配【歌手-歌名】格式
    protected String[] getArtistAndMusic(String fileName){
        String[] strings = new String[2];
        if (fileName.matches(".+[ ]+[-]{1}[ ]+.+")) {
            int temp = fileName.indexOf('-');
            strings[0] = fileName.substring(0, temp);
            //去除多余空格
            while (fileName.charAt(temp + 1) == ' ')
                temp++;
            strings[1] = fileName.substring(temp + 1);
        } else {
            strings[0] =  "未知歌手";
            strings[1] = fileName;
        }
        return strings;
    }

}
