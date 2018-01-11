package com.example.student.diary_project;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

public class MainActivity extends AppCompatActivity {
    private Button btnCalendar;
    private ImageButton btnThema, btnWrite, btnSetting;
    MaterialCalendarView calendarV;

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

//                ButterKnife.bind(this);
                MaterialCalendarView calendarV = (MaterialCalendarView)findViewById(R.id.calendarView);

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
