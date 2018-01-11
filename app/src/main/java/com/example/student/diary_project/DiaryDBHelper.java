package com.example.student.diary_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-11.
 */

public class DiaryDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DiaryDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성 (4개)

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 다이어트 일기 쓴 날짜 다 불러오기
    public List<Date> selectDietAllDate() {
        List<Date> dates = new ArrayList<>();

        return dates;
    }
}
