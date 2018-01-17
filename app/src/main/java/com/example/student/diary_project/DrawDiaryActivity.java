package com.example.student.diary_project;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-12.
 */

public class DrawDiaryActivity extends AppCompatActivity {
    private Button btnCalendar;
    private ImageButton btnThema, btnSave, btnOut;
    private ImageButton btnColor, btnEraser, btnPrev, btnClear;
    private EditText drawEdit;
    private GridView drawGridView;

    private Date mDate;
    private String year, month, day;
    private int checkColorMenu = 0;

    private DrawView drawView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_draw);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //뒤로가기 버튼 구현
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        ///////////////////////////////////////////////////////////////////////////////////

        //레이아웃 아이디 모음
        btnCalendar = findViewById(R.id.btn_draw_calendar);
        btnColor = findViewById(R.id.btn_draw_color);
        btnEraser = findViewById(R.id.btn_draw_eraser);
        btnPrev = findViewById(R.id.btn_draw_prev);
        btnClear = findViewById(R.id.btn_draw_clear);
        btnThema = findViewById(R.id.btn_draw_thema);
        btnSave = findViewById(R.id.btn_draw_save);
        btnOut = findViewById(R.id.btn_draw_dd);
        drawEdit = findViewById(R.id.draw_edit);
        drawGridView = findViewById(R.id.draw_gridView);
        drawView = (DrawView) findViewById(R.id.draw_view);

        //////////////////////////////////////////////////////////////////////////////////

        //현재 시간 표시
        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
        SimpleDateFormat daySdf = new SimpleDateFormat("dd");

        year = yearSdf.format(new Date());
        month = monthSdf.format(new Date());
        day = daySdf.format(new Date());

        btnCalendar.setText(year + "." + month + "." + day);
        /////////////////////////////////////////////////////////////////////////////

        //그리기 색상 표시 디자인하기 위해 그리드 뷰로 구성
        List<Integer> colorList = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            colorList.add(x);
        }

        DrawGridViewAdapter adapter = new DrawGridViewAdapter(this, R.layout.item_colorbutton, colorList);
        drawGridView.setAdapter(adapter);
        ////////////////////////////////////////////////////////////////////////////

        btnCalendar.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_calendar_day);

                //start 달력 데코 (주말 색 표시,오늘 날짜 색 표시)
                MaterialCalendarView materialCalendarView = (MaterialCalendarView) dialog.findViewById(R.id.dialog_calendar);
                materialCalendarView.addDecorators(
                        new CalendarSaturdayDecorate(),
                        new CalendarSundayDecorate(),
                        new CalendarTodayDecorate());
                //달력 데코 끝

                materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
                        SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
                        SimpleDateFormat daySdf = new SimpleDateFormat("dd");

                        mDate = date.getDate();
                        year = yearSdf.format(mDate);
                        month = monthSdf.format(mDate);
                        day = daySdf.format(mDate);

                        btnCalendar.setText(year + "." + month + "." + day);

                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

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
//                drawView.eraser();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
