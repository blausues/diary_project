package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.student.diary_project.vo.DrawingVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2018-01-19.
 */

public class DrawDBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "draw.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public DrawDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS DRAW_TABLE "
                + "(WRITE_DATE DATE PRIMARY KEY, CONTENT TEXT, FILENAME VARCHAR(50),THEME INTEGER);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DRAW_TABLE");
        onCreate(db);
    }

    //그림 일기 추가
    public void insertDrawDiary(DrawingVO drawing){
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE",drawing.getDrawDate());
        values.put("CONTENT",drawing.getDrawContent());
        values.put("FILENAME",drawing.getDrawFileName());
        values.put("THEME",1);

        db.insert("DRAW_TABLE",null,values);
    }

    //읽을 일기 불러오기
    public DrawingVO selectDrawDiary(String drawdate){
        String sql = "SELECT WRITE_DATE,CONTENT,FILENAME,THEME FROM DRAW_TABLE WHERE WRITE_DATE='" + drawdate + "';";
        Cursor cursor = db.rawQuery(sql,null);

        DrawingVO drawingVO = new DrawingVO();

        if(cursor.moveToNext()){
            drawingVO.setDrawDate(cursor.getString(0));
            drawingVO.setDrawContent(cursor.getString(1));
            drawingVO.setDrawFileName(cursor.getString(2));
            drawingVO.setTheme(cursor.getInt(3));
        }
        return drawingVO;
    }

    //일기 판별용 카운터
    public int selectDrawDiaryCount(String drawdate){
        String sql = "SELECT COUNT(*) FROM DRAW_TABLE WHERE WRITE_DATE LIKE '%"+drawdate+"%';";
        Cursor cursor = db.rawQuery(sql,null);

        int drawDiaryCount = 0;

        if(cursor.moveToNext()){
            drawDiaryCount = cursor.getInt(0);
        }
        Log.d("jw","db실행"+drawDiaryCount);
        return drawDiaryCount;
    }

    //날짜별로 불러오기 drawdate는 불러올 날짜
    public List<DrawingVO> selectDrawDiaryList(String drawdate){
        String sql = "SELECT WRITE_DATE,CONTENT,THEME FROM DRAW_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%' ORDER BY WRITE_DATE ASC;";
        Cursor cursor = db.rawQuery(sql,null);

        List<DrawingVO> drawingVOList = new ArrayList<>();

        while(cursor.moveToNext()){
            DrawingVO drawingVO = new DrawingVO();
            drawingVO.setDrawDate(cursor.getString(0));
            drawingVO.setDrawContent(cursor.getString(1));
            drawingVO.setTheme(cursor.getInt(2));
            drawingVOList.add(drawingVO);
        }
        return drawingVOList;
    }

    //일기 수정
    public void updateDrawDiary(DrawingVO drawingVO){
        ContentValues values = new ContentValues();
        values.put("CONTENT",drawingVO.getDrawContent());

        db.update("DRAW_TABLE",values,"WRITE_DATE=?",new String[] {drawingVO.getDrawDate()});
    }

    //일기 삭제
    public void deleteDrawDiary(String draw_date){
        String sql = "DELETE FROM DRAW_TABLE WHERE WRITE_DATE='" + draw_date +"';";
        db.execSQL(sql);
    }
}
