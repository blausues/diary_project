package com.example.student.diary_project;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by student on 2018-01-10.
 */

// theme 0:일반, 1:그림, 2:금연, 3:다이어트
public class MainMonthActivity extends Activity {
    private MaterialCalendarView calendarView;
    private ImageButton btnMonthTheme, btnMonthWrite, btnMonthSetting;
    private int theme = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        calendarView = findViewById(R.id.calendarView);
        btnMonthTheme = findViewById(R.id.btn_month_theme);
        btnMonthWrite = findViewById(R.id.btn_month_write);
        btnMonthSetting = findViewById(R.id.btn_month_setting);

        // DB에서 해당 테마 일기 쓴 날짜 가져와서 List에 넣기
        List<CalendarDay> dates = new ArrayList<>();
        // 오늘 날짜 가져오기
        Calendar calendar = Calendar.getInstance();
        // CalendarDay.from -> CalendarDay(year, month, day) return
        CalendarDay day = CalendarDay.from(calendar);
        dates.add(day);
        calendarView.addDecorator(new EventDecorator(Color.GRAY, dates));

        calendarView.addDecorators(new CalendarSundayDecorate(), new CalendarSaturdayDecorate(), new CalendarTodayDecorate());

        // 달력 클릭 시, 일기 읽는 화면으로
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

            }
        });

        // 테마 버튼 클릭 시, 테마 메뉴 나오게
        btnMonthTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 글쓰기 버튼 클릭 시, 일기 쓰는 화면으로
        btnMonthWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 세팅 버튼 클릭 시, 세팅 화면으로
        btnMonthSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // 날짜 밑에 원 그리는 decorator
    public class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(10, color));
        }
    }
}
