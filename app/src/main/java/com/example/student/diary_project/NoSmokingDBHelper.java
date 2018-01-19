package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.student.diary_project.vo.NoSmokingVO;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-11.
 */

public class NoSmokingDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public NoSmokingDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        String sql = "CREATE TABLE IF NOT EXISTS NOSMOKING_TABLE " +
                "(WRITE_DATE DATE PRIMARY KEY, START_DATE DATE, GIVE_UP INTEGER, PROMISE TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NOSMOKING_TABLE");
        onCreate(db);
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 금연 일기

    // 금연 일기 쓴 날짜 다 불러오기
    public List<Calendar> selectNoSmokingAllDate() {
        String sql = "SELECT WRITE_DATE FROM NOSMOKING_TABLE";

        Cursor cursor = db.rawQuery(sql, null);

        List<Calendar> dates = new ArrayList<>();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

        while(cursor.moveToNext()) {
            Calendar calendar = Calendar.getInstance();

            Date date = transFormat.parse(cursor.getString(0), new ParsePosition(0));

            calendar.setTime(date);

            dates.add(calendar);
        }
        return dates;
    }

    // 금연 일기 최근에 쓴 것 불러오기
    public NoSmokingVO selectNoSmokingLastDate() {
        String sql = "SELECT START_DATE, GIVE_UP FROM NOSMOKING_TABLE WHERE WRITE_DATE = " +
                "(SELECT MAX(WRITE_DATE) FROM NOSMOKING_TABLE)";

        Cursor cursor = db.rawQuery(sql, null);

        NoSmokingVO noSmokingVO = new NoSmokingVO();

        if(cursor.moveToNext()) {
            noSmokingVO.setStartDate(cursor.getString(0));
            noSmokingVO.setGiveUp(cursor.getInt(1));
        }
        return noSmokingVO;
    }

    // 해당 날짜에 쓴 금연 일기 불러오기
    public NoSmokingVO selectNoSmokingDate(String date) {
        String sql = "SELECT WRITE_DATE, START_DATE, GIVE_UP, PROMISE FROM NOSMOKING_TABLE WHERE WRITE_DATE = '" + date + "'";

        Cursor cursor = db.rawQuery(sql, null);

        NoSmokingVO noSmokingVO = new NoSmokingVO();

        if(cursor.moveToNext()) {
            noSmokingVO.setWriteDate(cursor.getString(0));
            noSmokingVO.setStartDate(cursor.getString(1));
            noSmokingVO.setGiveUp(cursor.getInt(2));
            noSmokingVO.setPromise(cursor.getString(3));
        }
        return noSmokingVO;
    }

    public void insertNoSmoking(NoSmokingVO noSmokingVO) {
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE", noSmokingVO.getWriteDate());
        values.put("START_DATE", noSmokingVO.getStartDate());
        values.put("GIVE_UP", noSmokingVO.getGiveUp());
        values.put("PROMISE", noSmokingVO.getPromise());

        db.insert("NOSMOKING_TABLE", null, values);
    }

    public void updateNoSmoking(NoSmokingVO noSmokingVO) {
        ContentValues values = new ContentValues();

        values.put("GIVE_UP", noSmokingVO.getGiveUp());
        values.put("PROMISE", noSmokingVO.getPromise());

        db.update("NOSMOKING_TABLE", values, "WRITE_DATE = ?", new String[]{noSmokingVO.getWriteDate()});
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}