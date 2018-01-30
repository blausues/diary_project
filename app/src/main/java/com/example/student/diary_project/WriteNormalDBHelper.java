package com.example.student.diary_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;
import android.widget.Toast;

import com.example.student.diary_project.vo.NormalVO;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by student on 2018-01-23.
 */

public class WriteNormalDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "normalDiary.db";
    private static final int DB_VERSION = 1;

    private SQLiteDatabase db;

    public WriteNormalDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS NORMAL_TABLE " +
                "(WRITE_DATE DATE PRIMARY KEY, NORMAL_CONTENT TEXT,IMAGE_PATH TEXT, THEME INTEGER DEFAULT 0)";
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
    public NormalVO selectNormalDiary(String drawdate) {
        String sql = "SELECT * FROM NORMAL_TABLE WHERE WRITE_DATE='" + drawdate + "';";
        Cursor cursor = db.rawQuery(sql, null);

        NormalVO normalVO = new NormalVO();
        ArrayList<String> imagePathList = new ArrayList<>();

        if (cursor.moveToNext()) {
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
    public List<NormalVO> selectNormalDiaryList(String drawdate) {
        String sql = "SELECT WRITE_DATE,NORMAL_CONTENT,THEME FROM NORMAL_TABLE WHERE WRITE_DATE LIKE '%" + drawdate + "%' ORDER BY WRITE_DATE ASC;";
        Cursor cursor = db.rawQuery(sql, null);

        List<NormalVO> normalVOArrayList = new ArrayList<>();

        while (cursor.moveToNext()) {
            NormalVO normalVO = new NormalVO();
            normalVO.setNormalWriteDate(cursor.getString(0));
            normalVO.setNormalWriteContent(cursor.getString(1));
            normalVO.setTheme(cursor.getInt(2));
            normalVOArrayList.add(normalVO);
        }
        return normalVOArrayList;
    }

    //일기 삭제
    public void deleteNormalDiary(String write_date){
        String sql = "DELETE FROM NORMAL_TABLE WHERE WRITE_DATE='" + write_date +"';";
        db.execSQL(sql);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Calendar> selectNormalAllDate() {
        String sql = "SELECT WRITE_DATE FROM NORMAL_TABLE;";

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

    public int selectWriteDate(String writeDate) {
        String sql = "SELECT WRITE_DATE FROM NORMAL_TABLE WHERE WRITE_DATE='" + writeDate + "'";
        int mode = 0;
        String result = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        if (result != null) {
            mode = 1;
        } else {
            mode = 0;
        }
        return mode;
    }

    public int insertNormal(NormalVO normalVO) {
        ContentValues values = new ContentValues();

        values.put("WRITE_DATE", normalVO.getNormalWriteDate());
        values.put("NORMAL_CONTENT", normalVO.getNormalWriteContent());
        values.put("IMAGE_PATH", String.valueOf(normalVO.getNormalWriteImagePath()));
        return (int) db.insert("NORMAL_TABLE", null, values);
    }

    public NormalVO selectAll(String writeDate) {
        String sql = "SELECT NORMAL_CONTENT,IMAGE_PATH FROM NORMAL_TABLE WHERE WRITE_DATE='" + writeDate + "'";
        NormalVO normalVO = new NormalVO();
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            normalVO.setNormalWriteContent(cursor.getString(0));
            normalVO.setNormalWriteImagePath(tokenizer(cursor.getString(1)));
        }
        return normalVO;
    }

    public int update(NormalVO normalVO) {
        String sql = "UPDATE NORMAL_TABLE SET NORMAL_CONTENT='" + normalVO.getNormalWriteContent() + "', IMAGE_PATH='" + normalVO.getNormalWriteImagePath() + "' WHERE WRITE_DATE='" + normalVO.getNormalWriteDate() + "'";
        db.execSQL(sql);
        return 1;
    }

    public ArrayList<String> tokenizer(String ImagePath) {
        ArrayList<String> imagePathList = new ArrayList<>();
        if (ImagePath.equals("null")) {
            imagePathList.clear();
        } else {
            String img = ImagePath.substring(1, ImagePath.length() - 1);
            StringTokenizer tokens = new StringTokenizer(img, ", ");
            for (int i = 1; tokens.hasMoreElements(); i++) {
                imagePathList.add(i - 1, tokens.nextToken());
            }
        }
        return imagePathList;
    }
}
