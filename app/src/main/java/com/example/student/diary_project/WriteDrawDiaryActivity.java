package com.example.student.diary_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.DrawingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by student on 2018-01-12.
 */

public class WriteDrawDiaryActivity extends Activity {
    private TextView tvDate;
    private Button btnSave;
    private ImageButton btnColor, btnEraser, btnPrev, btnClear;
    private EditText drawEdit;
    private GridView drawGridView;

    private String filename, writeDate, viewDate;
    private SimpleDateFormat currentDate;
    private int checkColorMenu = 0;
    private int dayMonthYearCheck, theme;
    private int activityCheck;

    private DrawView drawView;

    private DrawDBHelper drawDBHelper;
    private DrawingVO drawingVO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_draw);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        ////////////////////////////////////////////////////////////////////////////////////////
        //액티비티 생성

        drawDBHelper = new DrawDBHelper(this);
        drawingVO = new DrawingVO();
        ///////////////////////////////////////////////////////////////////////////////////////////

        //레이아웃 아이디 모음
        tvDate = findViewById(R.id.tv_draw_date);
        btnColor = findViewById(R.id.btn_draw_color);
        btnEraser = findViewById(R.id.btn_draw_eraser);
        btnPrev = findViewById(R.id.btn_draw_prev);
        btnClear = findViewById(R.id.btn_draw_clear);
        btnSave = findViewById(R.id.btn_draw_save);
        drawEdit = findViewById(R.id.draw_edit);
        drawGridView = findViewById(R.id.draw_gridView);
        drawView = (DrawView) findViewById(R.id.draw_view);

        //////////////////////////////////////////////////////////////////////////////////
        //인텐트 값 가져오기
        Intent intent = getIntent();
        writeDate = intent.getStringExtra("selectedDate");
        viewDate = intent.getStringExtra("viewDate");
        dayMonthYearCheck = intent.getIntExtra("dayMonthYearCheck", 0);
        theme = intent.getIntExtra("theme", 0);
        activityCheck = intent.getIntExtra("activityCheck", 0);

        //작성할 일정 표시
        tvDate.setText(writeDate);
        //////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////
        //그리기 색상 표시 디자인하기 위해 그리드 뷰로 구성
        List<Integer> colorList = new ArrayList<>();
        for (
                int x = 0;
                x < 5; x++) {
            colorList.add(x);
        }

        DrawGridViewAdapter adapter = new DrawGridViewAdapter(this, R.layout.item_colorbutton, colorList);
        drawGridView.setAdapter(adapter);
        ////////////////////////////////////////////////////////////////////////////

        btnColor.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                keyboardDown();
                if (checkColorMenu == 0) {
                    drawGridView.setVisibility(View.VISIBLE);
                    drawGridView.setFocusable(true);
                    drawGridView.setFocusableInTouchMode(true);
                    drawGridView.requestFocus();
                    drawGridView.bringToFront();
                    checkColorMenu = 1;
                } else {
                    drawGridView.setVisibility(View.INVISIBLE);
                    checkColorMenu = 0;
                }

            }
        });

        drawGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        drawView.selColor(Color.RED);
                        break;
                    case 1:
                        drawView.selColor(Color.BLUE);
                        break;
                    case 2:
                        drawView.selColor(Color.rgb(255, 165, 000));
                        break;
                    case 3:
                        drawView.selColor(Color.BLACK);
                        break;
                    case 4:
                        drawView.selColor(Color.GREEN);
                        break;
                }
                drawGridView.setVisibility(View.INVISIBLE);
                checkColorMenu = 0;
                keyboardDown();
            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.eraser(Color.WHITE);
                keyboardDown();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.printBack();
                keyboardDown();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setClear();
                keyboardDown();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                Bitmap myViewBitmap = getBitmapFromView(drawView);
                File result = screenShotSave(myViewBitmap);
                drawingVO.setDrawDate(writeDate);
                drawingVO.setDrawContent(drawEdit.getText() + "");
                drawingVO.setDrawFileName(filename);
                drawDBHelper.insertDrawDiary(drawingVO);

                //저장뒤 바로 읽기화면
                Intent intent = new Intent(WriteDrawDiaryActivity.this, DrawDiaryActivity.class);
                intent.putExtra("selectedDate", writeDate);
                intent.putExtra("viewDate", viewDate);
                intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
                intent.putExtra("theme", theme);
                intent.putExtra("activityCheck",activityCheck);
                startActivity(intent);
                finish();
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private File screenShotSave(Bitmap screenBitmap) {

        filename = new Random().nextInt(1000000000) + "s.jpg";
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/DCIM/Test1/" + filename);
        Toast.makeText(WriteDrawDiaryActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //키보드

    //키보드 내리기
    public void keyboardDown() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawEdit.getWindowToken(), 0);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //뒤로 가기
    @Override
    public void onBackPressed() {
        Intent intent;
        if (activityCheck == 0) {
            intent = new Intent(WriteDrawDiaryActivity.this, MainActivity.class);
        } else {
            intent = new Intent(WriteDrawDiaryActivity.this, MainMonthActivity.class);
        }
        intent.putExtra("selectedDate", writeDate);
        intent.putExtra("viewDate", viewDate);
        intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
        intent.putExtra("theme", theme);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
