package com.example.student.diary_project;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2018-01-12.
 */

public class DrawDiaryActivity extends AppCompatActivity {
    private Button btnCalendar;
    private ImageButton btnThema,btnSave,btnOut;
    private ImageButton btnColor,btnEraser,btnPrev,btnClear;
    private EditText drawEdit;
    private String year,month,day;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_draw);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        btnCalendar = findViewById(R.id.btn_draw_calendar);
        btnColor = findViewById(R.id.btn_draw_color);
        btnEraser = findViewById(R.id.btn_draw_eraser);
        btnPrev = findViewById(R.id.btn_draw_prev);
        btnClear = findViewById(R.id.btn_draw_clear);
        btnThema = findViewById(R.id.btn_draw_thema);
        btnSave = findViewById(R.id.btn_draw_save);
        btnOut = findViewById(R.id.btn_draw_dd);
        drawEdit = findViewById(R.id.draw_edit);

        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
        SimpleDateFormat daySdf = new SimpleDateFormat("dd");

        year = yearSdf.format(new Date());
        month = monthSdf.format(new Date());
        day = daySdf.format(new Date());

        btnCalendar.setText(year + "." + month + "." + day);



        btnCalendar.setOnClickListener(new View.OnClickListener() {
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

                        Date mDate = date.getDate();
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

        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }
}
