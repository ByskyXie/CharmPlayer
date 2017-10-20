package com.github.bysky.charmplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by asus on 2017/10/18.
 */

public class MusicDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_DB_COMMAND = "CREATE TABLE MUSIC_PATH(" +
            "FILE_PATH text," +
            "MUSIC_NAME text," +
            "PRIMARY KEY(FILE_PATH,MUSIC_NAME)" +
            ");";

    private Context context;
    public MusicDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_COMMAND);
            Log.w("MusicDatabaseOpenHelper","Execute create table ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO:还没重写
    }

}
