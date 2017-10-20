package com.github.bysky.charmplayer;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import java.io.File;

/**
 * Created by asus on 2017/10/18.
 */

public class BaseActivity extends AppCompatActivity {
    protected Button play_pause,back_music,next_music;
    protected static SQLiteDatabase musicSQLiteDatabases;
    protected static MusicDatabaseOpenHelper musicDatabaseOpenHelper=null;
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
    }
    private void checkMusicFile(){
        //TODO:可能出现大问题
        //需要用户允许读取内置存储空间权限
        Cursor all_music = musicSQLiteDatabases.query("MUSIC_PATH",new String[]{"FILE_PATH","MUSIC_NAME"},null,null,null,null,null);
        if(!all_music.moveToFirst())
            return;
        //不为空则为真能移至顶部
        String path,name;
        File music_file;
        do{
            path = all_music.getString(all_music.getColumnIndex("FILE_PATH"));
            name = all_music.getString(all_music.getColumnIndex("MUSIC_NAME"));
            music_file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+path,name);
            Log.e("BaseActivity",".checkMusicFile:"+music_file.getAbsolutePath());
            if(!music_file.exists()){
                //不存在则删除
                musicSQLiteDatabases.delete("MUSIC_PATH","FILE_PATH=? MUSIC_NAME=?",new String[]{path,name});
            }
        }while(all_music.moveToNext());
    }
}
