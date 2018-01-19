package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.student.diary_project.vo.DrawingVO;

import java.util.List;

/**
 * Created by student on 2018-01-19.
 */

public class DrawDBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "diary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DrawDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS DRAW_TABLE "
                + "(WRITE_DATE DATE,PRIMARY KEY, CONTENT TEXT, FILENAME VARCHAR(50),THEME INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DRAW_TABLE");
        onCreate(db);
    }

    public void insertDrawDiary(DrawingVO drawing){
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE",drawing.getDrawDate());
        values.put("CONTENT",drawing.getDrawContent());
        values.put("FILENAME",drawing.getDrawFileName());
        values.put("THEME",1);

        db.insert("DRAW_TABLE",null,values);
    }

    public List<DrawingVO> selectDrawList(String drawdate){
        String sql = "SELECT WRITE_DATE,CONTENT,FILENAME,THEME FROM DRAW_TABLE WHERE WRITE_DATE LIKE '%'" +drawdate
    }
}
