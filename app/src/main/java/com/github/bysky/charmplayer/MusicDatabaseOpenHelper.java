package com.github.bysky.charmplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by asus on 2017/10/18.
 */

public class MusicDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_DB_COMMAND = "CREATE TABLE MUSIC(" +
            "FILE_PATH TEXT PRIMARY KEY," +
            "FILE_NAME TEXT NOT NULL," +
            "FILE_FOLDER TEXT NOT NULL," +
            "MUSIC_NAME TEXT," +
            "ARTIST TEXT"+
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
