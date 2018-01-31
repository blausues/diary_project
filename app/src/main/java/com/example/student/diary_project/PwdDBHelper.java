package com.example.student.diary_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by student on 2018-01-31.
 */

public class PwdDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pwd.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public PwdDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS PWD_TABLE (PWD INTEGER PRIMARY KEY)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS PWD_TABLE");
        onCreate(db);
    }

    public int selectPwd() {
        String sql = "SELECT * FROM PWD_TABLE";

        Cursor cursor = db.rawQuery(sql, null);

        int pwd = 0;

        if(cursor.moveToNext()) {
            pwd = cursor.getInt(0);
        }
        return pwd;
    }

    public void insertPwd(int pwd) {
        String sql = "INSERT INTO PWD_TABLE(PWD) VALUES('"+pwd+"')";
        db.execSQL(sql);
    }

    public void updatePwd(int pwd) {
        String sql = "UPDATE PWD_TABLE SET PWD = '"+pwd+"')";
        db.execSQL(sql);
    }

    public void deletePwd() {
        String sql = "DELETE FROM PWD_TABLE";
        db.execSQL(sql);
    }
}
