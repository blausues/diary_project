package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by student on 2018-01-10.
 */

// theme 0:일반, 1:그림, 2:금연, 3:다이어트 4:전체
public class MainMonthActivity extends Activity {
    private MaterialCalendarView calendarView;
    private ImageButton btnMonthTheme, btnMonthWrite, btnMonthSetting;
    private TextView tvProgress, tvMaxProgress;
    private ProgressBar pbDiary;

    private int theme = 2;

    private List<CalendarDay> dates;

    private DiaryDBHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month);

        calendarView = findViewById(R.id.calendarView);
        btnMonthTheme = findViewById(R.id.btn_month_theme);
        btnMonthWrite = findViewById(R.id.btn_month_write);
        btnMonthSetting = findViewById(R.id.btn_month_setting);
        tvProgress = findViewById(R.id.tv_progress);
        tvMaxProgress = findViewById(R.id.tv_max_progress);
        pbDiary = findViewById(R.id.pb_diary);

        helper = new DiaryDBHelper(this);

        List<Calendar> tempDates = new ArrayList<>();
        dates = new ArrayList<>();

        // DB에서 해당 테마 일기 쓴 날짜 가져와서 List에 넣기
        if(theme == 0) {

        } else if(theme == 1) {

        } else if(theme == 2) {
            tempDates = helper.selectNoSmokingAllDate();

            NoSmokingVO noSmokingVO = helper.selectNoSmokingLastDate();

            // theme = 2, 3이면 마지막으로 쓴 일기의 시작날짜를 가져와서 progressBar에 그리기
            if(noSmokingVO.getGiveUp() == 0) {
                // giveUp 0:진행중, 1:포기
                Date today = new Date();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(noSmokingVO.getStartDate(), new ParsePosition(0));

                int dDay = (int) Math.floor((today.getTime() - startDate.getTime()) / 86400000) + 1;

                // progressBar에 d-day 표시해주기
                tvProgress.setText("D+"+dDay);
                tvMaxProgress.setText(((dDay/100) + 1) * 100+"");
                pbDiary.setProgress(dDay%100);
            }
            tvProgress.setVisibility(View.VISIBLE);
            tvMaxProgress.setVisibility(View.VISIBLE);
            pbDiary.setVisibility(View.VISIBLE);
        } else if(theme == 3) {

        } else if(theme == 4) {
            // 전체 리스트 화면으로 넘어가기
        }

        // Calendar 에서 CalderdarDay 로 변환
        for(int i=0; i<tempDates.size(); i++) {
            // CalendarDay.from -> CalendarDay(year, month, day) return
            CalendarDay day = CalendarDay.from(tempDates.get(i));
            dates.add(day);
        }
        // 해당 날짜들 밑에 동그라미 그리기
        calendarView.addDecorator(new EventDecorator(Color.RED, dates));
        calendarView.addDecorators(new CalendarSundayDecorate(), new CalendarSaturdayDecorate(), new CalendarTodayDecorate());

        // 달력 클릭 시, 일기 읽는 화면으로
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                for(int i=0; i<dates.size(); i++) {
                    if(dates.get(i).equals(date)) {
                        if(theme == 2) {
                            Intent intent = new Intent(MainMonthActivity.this, ShowNoSmokingActivity.class);

                            intent.putExtra("writeDate", date);
                            startActivity(intent);

                            break;
                        }
                    }
                }
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
                Intent intent = new Intent(MainMonthActivity.this, WriteDietActivity.class);
                startActivity(intent);
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

    // 뒤로가기 버튼을 통해서 왔을 때, 새로고침 되어야 한다!
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
