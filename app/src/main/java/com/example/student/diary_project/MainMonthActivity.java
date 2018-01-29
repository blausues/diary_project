package com.example.student.diary_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.DietVO;
import com.example.student.diary_project.vo.NoSmokingVO;
import com.example.student.diary_project.vo.NormalVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import im.dacer.androidcharts.LineView;

/**
 * Created by student on 2018-01-10.
 */

// theme 0:일반, 1:그림, 2:금연, 3:다이어트 4:전체
public class MainMonthActivity extends Activity {
    private MaterialCalendarView calendarView;
    private ImageButton btnMonthTheme, btnMonthWrite, btnMonthSetting;
    private TextView tvProgress, tvMaxProgress;
    private ProgressBar pbDiary;
    private LineView lineView;

    private int theme = 0;

    private List<CalendarDay> dates;

    private WriteNormalDBHelper writeNormalDBHelper;
    private NoSmokingDBHelper noSmokingDBHelper;
    private DietDBHelper dietDBHelper;

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
        lineView = findViewById(R.id.line_view);

        List<Calendar> tempDates = new ArrayList<>();
        dates = new ArrayList<>();

        Intent intent = getIntent();
        theme = intent.getIntExtra("theme", 2);

        // DB에서 해당 테마 일기 쓴 날짜 가져와서 List에 넣기
        if(theme == 0) {
            writeNormalDBHelper = new WriteNormalDBHelper(this);
            tempDates = writeNormalDBHelper.selectNormalAllDate();
            Toast.makeText(this, "일반이당", Toast.LENGTH_SHORT).show();
        } else if(theme == 1) {
            Toast.makeText(this, "그림이당", Toast.LENGTH_SHORT).show();
        } else if(theme == 2) {
            noSmokingDBHelper = new NoSmokingDBHelper(this);

            tempDates = noSmokingDBHelper.selectNoSmokingAllDate();

            NoSmokingVO noSmokingVO = noSmokingDBHelper.selectNoSmokingLastDate();

            // 마지막으로 쓴 일기의 시작날짜를 가져와서 progressBar에 그리기
            if(noSmokingVO.getGiveUp() == 0 && noSmokingVO.getStartDate() != null) {
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
            dietDBHelper = new DietDBHelper(this);

            tempDates = dietDBHelper.selectDietAllDate();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
            // 이번 달 체중 기록 가져오기
            ArrayList<DietVO> dietVOS = dietDBHelper.selectDietMonth(sdf.format(new Date()));

            if (dietVOS.size() > 0) {
                ArrayList<String> dateList = new ArrayList<>();
                ArrayList<Float> weightList = new ArrayList<>();

                for (int i = 0; i < dietVOS.size(); i++) {
                    dateList.add(dietVOS.get(i).getWriteDate().substring(5));
                    weightList.add(dietVOS.get(i).getWeight());
                }
                ArrayList<ArrayList<Float>> weightLists = new ArrayList<>();
                weightLists.add(weightList);

                lineView.setDrawDotLine(false); //optional
                lineView.setShowPopup(LineView.SHOW_POPUPS_All); //optional
                lineView.setBottomTextList(dateList);
                lineView.setColorArray(new int[]{0xFF6799FF});
                lineView.setFloatDataList(weightLists);
                lineView.setVisibility(View.VISIBLE);
            } else {
                lineView.setVisibility(View.GONE);
            }
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

        // 달 변경 시, 밑에 체중 그래프 바꿔주기
        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                if (theme == 3) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    // 이번 달 체중 기록 가져오기
                    ArrayList<DietVO> dietVOS = dietDBHelper.selectDietMonth(sdf.format(date.getDate()));

                    if (dietVOS.size() > 0) {
                        ArrayList<String> dateList = new ArrayList<>();
                        ArrayList<Float> weightList = new ArrayList<>();

                        for (int i = 0; i < dietVOS.size(); i++) {
                            dateList.add(dietVOS.get(i).getWriteDate().substring(5));
                            weightList.add(dietVOS.get(i).getWeight());
                        }
                        ArrayList<ArrayList<Float>> weightLists = new ArrayList<>();
                        weightLists.add(weightList);

                        lineView.setDrawDotLine(false); //optional
                        lineView.setShowPopup(LineView.SHOW_POPUPS_All); //optional
                        lineView.setBottomTextList(dateList);
                        lineView.setColorArray(new int[]{0xFF6799FF});
                        lineView.setFloatDataList(weightLists);
                        lineView.setVisibility(View.VISIBLE);
                    } else {
                        lineView.setVisibility(View.GONE);
                    }
                }
            }
        });

        // 달력 클릭 시, 일기 읽는 화면으로
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = sdf.format(date.getDate());

                if (theme == 0) {
                    NormalVO normalVO = new NormalVO();
                    normalVO.setNormalWriteContent(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteContent());
                    normalVO.setNormalWriteImagePath(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteImagePath());
                    Intent intent = new Intent(MainMonthActivity.this,WriteNormalActivity.class);
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("content", normalVO.getNormalWriteContent());
                    intent.putExtra("imagePath", normalVO.getNormalWriteImagePath());
                    startActivity(intent);

                } else if (theme == 1) {
                    // 준완이는 show랑 write 나눈다고 해서 이렇게 해놓음
                    for (int i = 0; i < dates.size(); i++) {
                        if (dates.get(i).equals(date)) {
                            // 달력에 일기 있으면 show로
                        } else {
                            // 없으면 write로
                        }
                    }
                } else if (theme == 2) {
                    Intent intent = new Intent(MainMonthActivity.this, ShowNoSmokingActivity.class);

                    intent.putExtra("selectedDate", selectedDate);
                    startActivity(intent);
                } else if (theme == 3) {
                    Intent intent = new Intent(MainMonthActivity.this, ShowDietActivity.class);

                    intent.putExtra("selectedDate", selectedDate);
                    startActivity(intent);
                }
            }
        });

        // 테마 버튼 클릭 시, 테마 메뉴 나오게
        btnMonthTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeThemeDialog().show();
            }
        });

        // 글쓰기 버튼 클릭 시, 오늘 자 일기 쓰는 화면으로
        btnMonthWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = sdf.format(new Date());

                intent.putExtra("selectedDate", selectedDate);

                if(theme == 0) {
                    intent.setClass(MainMonthActivity.this,WriteNormalActivity.class);
                } else if(theme == 1) {

                } else if(theme == 2) {
                    intent.setClass(MainMonthActivity.this, ShowNoSmokingActivity.class);
                } else if(theme == 3) {
                    intent.setClass(MainMonthActivity.this, ShowDietActivity.class);
                }
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

    // 뒤로가기 버튼을 통해서 왔을 때, 새로고침
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private Dialog makeThemeDialog() {
        Dialog themeDialog = new Dialog(this);
        themeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        themeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        themeDialog.getWindow().setGravity(Gravity.BOTTOM);
        themeDialog.setContentView(R.layout.dialog_select_theme);

        Button btnThemeNormal = themeDialog.findViewById(R.id.btn_theme_normal);
        Button btnThemeDraw = themeDialog.findViewById(R.id.btn_theme_draw);
        Button btnThemeNoSmoking = themeDialog.findViewById(R.id.btn_theme_nosmoking);
        Button btnThemeDiet = themeDialog.findViewById(R.id.btn_theme_diet);

        btnThemeNormal.setOnClickListener(new themeSelectListener());
        btnThemeDraw.setOnClickListener(new themeSelectListener());
        btnThemeNoSmoking.setOnClickListener(new themeSelectListener());
        btnThemeDiet.setOnClickListener(new themeSelectListener());

        return  themeDialog;
    }

    private class themeSelectListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_theme_normal:
                    theme = 0;
                    break;
                case R.id.btn_theme_draw:
                    theme = 1;
                    break;
                case R.id.btn_theme_nosmoking:
                    theme = 2;
                    break;
                case R.id.btn_theme_diet:
                    theme = 3;
                    break;
            }
            Intent intent = new Intent(MainMonthActivity.this, MainMonthActivity.class);
            intent.putExtra("theme", theme);

            startActivity(intent);
        }
    }

}
