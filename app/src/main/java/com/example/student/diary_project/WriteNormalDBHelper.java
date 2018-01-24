package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.student.diary_project.vo.NormalVO;

import java.util.ArrayList;
import java.util.List;

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
        db.execSQL("DROP TABLE IF EXISTS NORMAL_TABLE");
        onCreate(db);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //메인화면 리스트 불러오기 위한 db 내가 따로 작성 했어요
    //일기 판별용 카운터
    public int selectNormalDiaryCount(String drawdate) {
        String sql = "SELECT COUNT(*) FROM NORMAL_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%';";
        Cursor cursor = db.rawQuery(sql, null);

        int normalDiaryCount = 0;

        if (cursor.moveToNext()) {
            normalDiaryCount = cursor.getInt(0);
        }
        Log.d("jw", "db실행" + normalDiaryCount);
        return normalDiaryCount;
    }

    //일별 선택 및 읽을 일기 불러오기
    public NormalVO selectNormalDiary(String drawdate){
        String sql = "SELECT * FROM NORMAL_TABLE WHERE WRITE_DATE='" + drawdate + "';";
        Cursor cursor = db.rawQuery(sql,null);

        NormalVO normalVO = new NormalVO();
        ArrayList<String> imagePathList = new ArrayList<>();

        if(cursor.moveToNext()){
            normalVO.setNormalWriteDate(cursor.getString(0));
            normalVO.setNormalWriteContent(cursor.getString(1));
            imagePathList.add(cursor.getString(2));
            imagePathList.add(cursor.getString(3));
            imagePathList.add(cursor.getString(4));
            imagePathList.add(cursor.getString(5));
            imagePathList.add(cursor.getString(6));
            normalVO.setNormalWriteImagePath(imagePathList);
            normalVO.setTheme(cursor.getInt(7));
        }
        return normalVO;
    }

    //날짜별로 불러오기 drawdate는 불러올 날짜
    public List<NormalVO> selectNormalDiaryList(String drawdate){
        String sql = "SELECT WRITE_DATE,NORMAL_CONTENT,THEME FROM NORMAL_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%' ORDER BY WRITE_DATE ASC;";
        Cursor cursor = db.rawQuery(sql,null);

        List<NormalVO> normalVOArrayList = new ArrayList<>();

        while(cursor.moveToNext()){
            NormalVO normalVO = new NormalVO();
            normalVO.setNormalWriteDate(cursor.getString(0));
            normalVO.setNormalWriteContent(cursor.getString(1));
            normalVO.setTheme(cursor.getInt(2));
            normalVOArrayList.add(normalVO);
        }
        return normalVOArrayList;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
