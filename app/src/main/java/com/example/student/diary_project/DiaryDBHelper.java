package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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

public class DiaryDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DiaryDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성 (4개)
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
//            calendar.add(Calendar.MONTH, 1);

            dates.add(calendar);
        }
        return dates;
    }

    // 금연 일기 최근에 쓴 것 불러오기
    public NoSmokingVO selectNoSmokingLastDate() {
        String sql = "SELECT START_DATE, GIVE_UP FROM NOSMOKING_TABLE WHERE WRITE_DATE = " +
                "(SELECT MAX(WRITE_DATE) FROM NOSMOKING_TABLE)";

        Cursor cursor = db.rawQuery(sql, null);

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

        NoSmokingVO noSmokingVO = new NoSmokingVO();

        if(cursor.moveToNext()) {
            Date startDate = transFormat.parse(cursor.getString(0), new ParsePosition(0));
            noSmokingVO.setStartDate(startDate);

            noSmokingVO.setGiveUp(cursor.getInt(1));
            Log.i("lyh", startDate+"///"+cursor.getString(0)+"///"+cursor.getInt(1));
        }
        return noSmokingVO;
    }

    // 해당 날짜에 쓴 금연 일기 불러오기
    public NoSmokingVO selectNoSmokingDate(String date) {
        String sql = "SELECT WRITE_DATE, START_DATE, GIVE_UP, PROMISE FROM NOSMOKING_TABLE WHERE WRITE_DATE = '" + date + "'";

        Cursor cursor = db.rawQuery(sql, null);

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

        NoSmokingVO noSmokingVO = new NoSmokingVO();

        if(cursor.moveToNext()) {
            Date writeDate = transFormat.parse(cursor.getString(0), new ParsePosition(0));
            noSmokingVO.setWriteDate(writeDate);

            Date startDate = transFormat.parse(cursor.getString(1), new ParsePosition(0));
            noSmokingVO.setStartDate(startDate);

            noSmokingVO.setGiveUp(cursor.getInt(2));
            noSmokingVO.setPromise(cursor.getString(3));
        }
        return noSmokingVO;
    }

    public void insertNoSmoking(NoSmokingVO noSmokingVO) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date writeDate = noSmokingVO.getWriteDate();
        Date startDate = noSmokingVO.getStartDate();

        ContentValues values = new ContentValues();

//        values.put("WRITE_DATE", "2018-01-15");
//        values.put("START_DATE", "2018-01-03");
//        values.put("GIVE_UP", 2);

        values.put("WRITE_DATE", dateFormat.format(writeDate));
        values.put("START_DATE", dateFormat.format(startDate));
        values.put("GIVE_UP", noSmokingVO.getGiveUp());
        values.put("PROMISE", noSmokingVO.getPromise());

        db.insert("NOSMOKING_TABLE", null, values);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
