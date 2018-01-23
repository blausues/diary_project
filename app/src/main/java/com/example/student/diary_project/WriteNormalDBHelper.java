package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.student.diary_project.vo.NormalVO;

/**
 * Created by student on 2018-01-23.
 */

public class WriteNormalDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;
    public WriteNormalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS NORMAL_TABLE " +
                "(WRITE_DATE DATE PRIMARY KEY, NORMAL_CONTENT TEXT,IMAGE_PATH_0 TEXT, IMAGE_PATH_1 TEXT,IMAGE_PATH_2 TEXT,IMAGE_PATH_3 TEXT,IMAGE_PATH_4 TEXT, THEME INTEGER DEFAULT 0)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public int insertNormal(NormalVO normalVO) {
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE", normalVO.getNormalWriteDate());
        values.put("NORMAL_CONTEN", normalVO.getNormalWriteContent());
        values.put("IMAGE_PATH_0",normalVO.getNormalWriteImagePath().get(0));
        values.put("IMAGE_PATH_1",normalVO.getNormalWriteImagePath().get(1));
        values.put("IMAGE_PATH_2",normalVO.getNormalWriteImagePath().get(2));
        values.put("IMAGE_PATH_3",normalVO.getNormalWriteImagePath().get(3));
        values.put("IMAGE_PATH_4",normalVO.getNormalWriteImagePath().get(4));

        return (int) db.insert("NORMAL_TABLE", null, values);
    }
}
