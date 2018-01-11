package com.example.student.diary_project;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private Button btnCalendar;
    private ImageButton btnThema, btnWrite, btnSetting;
    private MaterialCalendarView materialCalendarView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        btnCalendar = findViewById(R.id.btn_calendar);
        btnThema = findViewById(R.id.btn_thema);
        btnWrite = findViewById(R.id.btn_write);
        btnSetting = findViewById(R.id.btn_setting);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.dialog_calendar);

                //start 달력 데코 (주말 색 표시,오늘 날짜 색 표시)
                materialCalendarView = (MaterialCalendarView)dialog.findViewById(R.id.dialog_calendar);
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
                        String year = yearSdf.format(mDate);
                        String month = monthSdf.format(mDate);
                        String day = daySdf.format(mDate);

                        btnCalendar.setText(year+"."+month+"."+day);



                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        btnThema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
