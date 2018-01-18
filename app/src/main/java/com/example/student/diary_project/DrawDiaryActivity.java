package com.example.student.diary_project;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-18.
 */

public class DrawDiaryActivity extends Activity {
    private LinearLayout showPaint;
    private ImageButton btnColor, btnEraser, btnPrev, btnClear;
    private Button btnUpdate;
    private EditText drawEdit;
    private GridView drawGridView;
    private TextView tvDate;

    private Date mDate;
    private String year, month, day;
    private int checkColorMenu = 0;

    private DrawView drawView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_draw);

        //레이아웃 아이디 모음
        btnColor = findViewById(R.id.btn_drawshow_color);
        btnEraser = findViewById(R.id.btn_drawshow_eraser);
        btnPrev = findViewById(R.id.btn_drawshow_prev);
        btnClear = findViewById(R.id.btn_drawshow_clear);
        btnUpdate = findViewById(R.id.btn_drawshow_update);
        drawEdit = findViewById(R.id.draw_edit);
        drawGridView = findViewById(R.id.drawshow_gridView);
        drawView = (DrawView) findViewById(R.id.drawshow_view);
        tvDate = findViewById(R.id.tv_drawshow_date);
        showPaint = findViewById(R.id.drawshow_paint);

        //////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////

        //선택한 일기 시간 표시
        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
        SimpleDateFormat daySdf = new SimpleDateFormat("dd");

        year = yearSdf.format(new Date());
        month = monthSdf.format(new Date());
        day = daySdf.format(new Date());

        tvDate.setText(year + "." + month + "." + day);
        ///////////////////////////////////////////////////////////////////////////

        //읽기 화면에서 수정화면 전환
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPaint.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
                return false;
            }
        });

        //그리기 색상 표시 디자인하기 위해 그리드 뷰로 구성
        List<Integer> colorList = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            colorList.add(x);
        }

        DrawGridViewAdapter adapter = new DrawGridViewAdapter(this, R.layout.item_colorbutton, colorList);
        drawGridView.setAdapter(adapter);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        btnColor.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
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

        drawGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                drawView.eraser(Color.WHITE);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.printBack();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                drawView.setClear();
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////
}
