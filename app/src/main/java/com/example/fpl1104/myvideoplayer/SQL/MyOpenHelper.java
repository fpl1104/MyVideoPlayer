package com.example.fpl1104.myvideoplayer.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fpl1104 on 16/6/28.
 */
public class MyOpenHelper extends SQLiteOpenHelper {
    public static final int VERSION=4;
    public MyOpenHelper(Context context) {
        super(context, "data.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql="create table User(_id integer primary key autoincrement,username,password,personage)";
//        String sql1="create table Personage(_id integer primary key autoincrement,username ,text varchar(200),video_uri varchar(200),profile_image varchar(200))";
        db.execSQL(sql);
//        db.execSQL(sql1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
