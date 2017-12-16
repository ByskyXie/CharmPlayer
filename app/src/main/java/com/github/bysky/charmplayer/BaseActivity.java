package com.github.bysky.charmplayer;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.File;
import java.io.Serializable;

/**
 * Created by asus on 2017/10/18.
 */

public class BaseActivity extends AppCompatActivity{

    protected static SQLiteDatabase musicSQLiteDatabases;//TODO:后期需要将转换为private
    protected static MusicDatabaseOpenHelper musicDatabaseOpenHelper;
    private static boolean hasCreateService = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(musicDatabaseOpenHelper == null){
            //TODO:后期创建其它表时可能失败，此时需要升级数据库（版本号）
            musicDatabaseOpenHelper = new MusicDatabaseOpenHelper(this,"CharmPlayer.db",null,1);
            musicSQLiteDatabases = musicDatabaseOpenHelper.getWritableDatabase();
        }
        //检查音乐文件存在否
        checkMusicFile();
        if(!hasCreateService){
            hasCreateService = true;
            startService(new Intent(this,BroadcastService.class));
        }
    }

    private void checkMusicFile(){
        //TODO:可能因无权限而闪退
        Cursor all_music = getSavedMusicList();
        if(!all_music.moveToFirst())
            return;
        //不为空则为真能移至顶部
        String path,name;
        File music_file;
        do{
            path = all_music.getString(all_music.getColumnIndex("FILE_PATH"));
            music_file = new File(path);
            if(!music_file.exists()){
                //不存在则删除 TODO:后期考虑放入已删除文件表，定时检查及清除记录以提高导入效率 + 考虑加入忽略的音乐文件表
                musicSQLiteDatabases.delete("MUSIC","FILE_PATH=?",new String[]{path});
            }
        }while(all_music.moveToNext());
    }

    protected Cursor getSavedMusicList(){
        return musicSQLiteDatabases.query("MUSIC",new String[]{"FILE_PATH","FILE_NAME","FILE_FOLDER","MUSIC_NAME","ARTIST"}
            ,null, null,null,null,"FILE_PATH DESC");
    }

    protected Cursor getSavedMusicList(String artist){
        return musicSQLiteDatabases.query("MUSIC",new String[]{"FILE_PATH","FILE_NAME","FILE_FOLDER","MUSIC_NAME","ARTIST"}
                ,"ARTIST=?", new String[]{artist},null,null,"FILE_PATH DESC");
    }

    protected Cursor getArtistList(){
        return musicSQLiteDatabases.query("MUSIC",new String[]{"ARTIST"}
                ,null, null,"ARTIST",null,"ARTIST ASC");
    }

    protected Cursor getFondList(){
        return musicSQLiteDatabases.query("FOND",new String[]{"FILE_PATH","FILE_NAME","FILE_FOLDER","MUSIC_NAME","ARTIST"}
                ,null, null,null,null,null);
    }

    protected void initialUI(){}

    protected void setButtonUnable(Button button){
        button.setEnabled(false);
        button.setTextColor(Color.GRAY);
    }

    protected void setButtonEnable(Button button){
        button.setEnabled(true);
        button.setTextColor(Color.WHITE);
    }

}
