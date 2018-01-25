package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.student.diary_project.vo.DietVO;
import com.example.student.diary_project.vo.NoSmokingVO;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-23.
 */

public class DietDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "diet.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DietDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS DIET_TABLE " +
                "(WRITE_DATE DATE PRIMARY KEY, WEIGHT REAL, PHOTO TEXT, MENU1 TEXT, KCAL1 REAL, MENU2 TEXT, KCAL2 REAL, " +
                "MENU3 TEXT, KCAL3 REAL, MEMO TEXT, THEME INTEGER DEFAULT 3);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DIET_TABLE");
        onCreate(db);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //메인화면 리스트 불러오기 위한 db 내가 따로 작성 했어요
    //일기 판별용 카운터
    public int selectDietDiaryCount(String drawdate) {
        String sql = "SELECT COUNT(*) FROM DIET_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%';";
        Cursor cursor = db.rawQuery(sql, null);

        int drawDiaryCount = 0;

        if (cursor.moveToNext()) {
            drawDiaryCount = cursor.getInt(0);
        }
        Log.d("jw", "db실행" + drawDiaryCount);
        return drawDiaryCount;
    }

    //일별 선택 및 읽을 일기 불러오기
    public DietVO selectDietDiary(String drawdate){
        String sql = "SELECT * FROM DIET_TABLE WHERE WRITE_DATE='" + drawdate + "';";
        Cursor cursor = db.rawQuery(sql,null);

        DietVO dietVO = new DietVO();

        if(cursor.moveToNext()){
            dietVO.setWriteDate(cursor.getString(0));
            dietVO.setWeight(cursor.getFloat(1));
            dietVO.setPhoto(cursor.getString(2));
            dietVO.setMenu1(cursor.getString(3));
            dietVO.setKcal1(cursor.getFloat(4));
            dietVO.setMenu2(cursor.getString(5));
            dietVO.setKcal2(cursor.getFloat(6));
            dietVO.setMenu3(cursor.getString(7));
            dietVO.setKcal3(cursor.getFloat(8));
            dietVO.setMemo(cursor.getString(9));
            dietVO.setTheme(cursor.getInt(10));
        }
        return dietVO;
    }

    //날짜별로 불러오기 drawdate는 불러올 날짜
    public List<DietVO> selectDietDiaryList(String drawdate){
        String sql = "SELECT WRITE_DATE,MEMO,THEME FROM DIET_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%' ORDER BY WRITE_DATE ASC;";
        Cursor cursor = db.rawQuery(sql,null);

        List<DietVO> dietVOArrayList = new ArrayList<>();

        while(cursor.moveToNext()){
            DietVO dietVO = new DietVO();
            dietVO.setWriteDate(cursor.getString(0));
            dietVO.setMemo(cursor.getString(1));
            dietVO.setTheme(cursor.getInt(2));
            dietVOArrayList.add(dietVO);
        }
        return dietVOArrayList;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // 일기 쓴 날짜 다 불러오기
    public List<Calendar> selectDietAllDate() {
        String sql = "SELECT WRITE_DATE FROM DIET_TABLE;";

        Cursor cursor = db.rawQuery(sql, null);

        List<Calendar> dates = new ArrayList<>();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

        while (cursor.moveToNext()) {
            Calendar calendar = Calendar.getInstance();

            Date date = transFormat.parse(cursor.getString(0), new ParsePosition(0));

            calendar.setTime(date);

            dates.add(calendar);
        }
        return dates;
    }

    // 최근 7일간 쓴 일기에서 체중 불러오기
    public ArrayList<DietVO> selectDietWeek() {
        String sql = "SELECT WRITE_DATE, WEIGHT FROM DIET_TABLE ORDER BY WRITE_DATE DESC LIMIT 7";

        ArrayList<DietVO> dietVOS = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            DietVO dietVO = new DietVO();

            dietVO.setWriteDate(cursor.getString(0));
            dietVO.setWeight(cursor.getFloat(1));

            dietVOS.add(dietVO);
        }
        return dietVOS;
    }

    // 해당 달에 쓴 일기에서 날짜, 체중 불러오기
    public ArrayList<DietVO> selectDietMonth(String selectedMonth){
        String sql = "SELECT WRITE_DATE, WEIGHT FROM DIET_TABLE WHERE WRITE_DATE LIKE '%" + selectedMonth + "%' ORDER BY WRITE_DATE ASC;";

        ArrayList<DietVO> dietVOS = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            DietVO dietVO = new DietVO();

            dietVO.setWriteDate(cursor.getString(0));
            dietVO.setWeight(cursor.getFloat(1));

            dietVOS.add(dietVO);
        }
        return dietVOS;
    }

    // 해당 날짜에 쓴 일기 불러오기
    public DietVO selectDietDate(String date) {
        String sql = "SELECT * FROM DIET_TABLE WHERE WRITE_DATE = '" + date + "'";

        Cursor cursor = db.rawQuery(sql, null);

        DietVO dietVO = new DietVO();

        if(cursor.moveToNext()) {
            dietVO.setWriteDate(cursor.getString(0));
            dietVO.setWeight(cursor.getFloat(1));
            dietVO.setPhoto(cursor.getString(2));
            dietVO.setMenu1(cursor.getString(3));
            dietVO.setKcal1(cursor.getFloat(4));
            dietVO.setMenu2(cursor.getString(5));
            dietVO.setKcal2(cursor.getFloat(6));
            dietVO.setMenu3(cursor.getString(7));
            dietVO.setKcal3(cursor.getFloat(8));
            dietVO.setMemo(cursor.getString(9));
        }
        return dietVO;
    }

    public int insertDiet(DietVO dietVO) {
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE", dietVO.getWriteDate());
        values.put("WEIGHT", dietVO.getWeight());
        values.put("PHOTO", dietVO.getPhoto());
        values.put("MENU1", dietVO.getMenu1());
        values.put("KCAL1", dietVO.getKcal1());
        values.put("MENU2", dietVO.getMenu2());
        values.put("KCAL2", dietVO.getKcal2());
        values.put("MENU3", dietVO.getMenu3());
        values.put("KCAL3", dietVO.getKcal3());
        values.put("MEMO", dietVO.getMemo());

        return (int) db.insert("DIET_TABLE", null, values);
    }

    public int updateDiet(DietVO dietVO) {
        ContentValues values = new ContentValues();

        values.put("WEIGHT", dietVO.getWeight());
        values.put("PHOTO", dietVO.getPhoto());
        values.put("MENU1", dietVO.getMenu1());
        values.put("KCAL1", dietVO.getKcal1());
        values.put("MENU2", dietVO.getMenu2());
        values.put("KCAL2", dietVO.getKcal2());
        values.put("MENU3", dietVO.getMenu3());
        values.put("KCAL3", dietVO.getKcal3());
        values.put("MEMO", dietVO.getMemo());

        return db.update("DIET_TABLE", values, "WRITE_DATE = ?", new String[]{dietVO.getWriteDate()});
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
