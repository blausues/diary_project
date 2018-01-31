package com.example.student.diary_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

// theme 0:일반, 1:그림, 2:금연, 3:다이어트
public class MainMonthActivity extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    private MaterialCalendarView calendarView;
    private ImageButton btnMonthTheme, btnMonthWrite, btnMonthSetting;
    private TextView tvProgress, tvMaxProgress;
    private ProgressBar pbDiary;
    private LineView lineView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SwitchCompat switcher;

    private int theme = 0;

    private List<CalendarDay> dates;

    private WriteNormalDBHelper writeNormalDBHelper;
    private DrawDBHelper drawDBHelper;
    private NoSmokingDBHelper noSmokingDBHelper;
    private DietDBHelper dietDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_month);

        calendarView = findViewById(R.id.calendarView);
        btnMonthTheme = findViewById(R.id.btn_month_theme);
        btnMonthWrite = findViewById(R.id.btn_month_write);
        btnMonthSetting = findViewById(R.id.btn_month_setting);
        tvProgress = findViewById(R.id.tv_progress);
        tvMaxProgress = findViewById(R.id.tv_max_progress);
        pbDiary = findViewById(R.id.pb_diary);
        lineView = findViewById(R.id.line_view);
        drawerLayout = findViewById(R.id.month_drawer_layout);
        navigationView = findViewById(R.id.month_nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.nav_password);
        View actionView = menuItem.getActionView();
        switcher = actionView.findViewById(R.id.switcher);
        switcher.setChecked(true);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (switcher.isChecked()) ? "is checked!!!" : "not checked!!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        List<Calendar> tempDates = new ArrayList<>();
        dates = new ArrayList<>();

        Intent receiveIntent = getIntent();
        theme = receiveIntent.getIntExtra("theme", 0);
        String viewDate = receiveIntent.getStringExtra("selectedDate");

        if(viewDate != null) {
            int year = Integer.parseInt(viewDate.substring(0,4));
            int month = Integer.parseInt(viewDate.substring(5,7));
            calendarView.setCurrentDate(CalendarDay.from(year, month-1, 1));
        }

        // DB에서 해당 테마 일기 쓴 날짜 가져와서 List에 넣기
        if(theme == 0) {
            writeNormalDBHelper = new WriteNormalDBHelper(this);
            tempDates = writeNormalDBHelper.selectNormalAllDate();
        } else if(theme == 1) {
            drawDBHelper = new DrawDBHelper(this);
//            tempDates =
            /////////////////////////////////////////////////////////////////////





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

                Intent intent = new Intent();
                intent.putExtra("selectedDate", selectedDate);
                intent.putExtra("activityCheck", 1);

                if (theme == 0) {
                    NormalVO normalVO = new NormalVO();
                    normalVO.setNormalWriteContent(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteContent());
                    normalVO.setNormalWriteImagePath(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteImagePath());

                    intent.setClass(MainMonthActivity.this,WriteNormalActivity.class);

                    intent.putExtra("content", normalVO.getNormalWriteContent());
                    intent.putExtra("imagePath", normalVO.getNormalWriteImagePath());
                    intent.putExtra("theme", 0);

                } else if (theme == 1) {



                    /////////////////////////////////////////////////////////////////////

                    // 준완이는 show랑 write 나눈다고 해서 이렇게 해놓음
                    for (int i = 0; i < dates.size(); i++) {
                        if (dates.get(i).equals(date)) {
                            // 달력에 일기 있으면 show로
                        } else {
                            // 없으면 write로
                        }
                    }
                } else if (theme == 2) {
                    intent.setClass(MainMonthActivity.this,ShowNoSmokingActivity.class);
                    intent.putExtra("theme", 2);
                } else if (theme == 3) {
                    intent.setClass(MainMonthActivity.this,ShowDietActivity.class);
                    intent.putExtra("theme", 3);
                }
                startActivity(intent);
                finish();
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
                    NormalVO normalVO = new NormalVO();
                    normalVO.setNormalWriteContent(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteContent());
                    normalVO.setNormalWriteImagePath(writeNormalDBHelper.selectAll(selectedDate).getNormalWriteImagePath());
                    intent.putExtra("selectedDate", selectedDate);
                    intent.putExtra("content", normalVO.getNormalWriteContent());
                    intent.putExtra("imagePath", normalVO.getNormalWriteImagePath());
                    intent.putExtra("theme", 0);
                    intent.setClass(MainMonthActivity.this, WriteNormalActivity.class);
                } else if(theme == 1) {

                    ///////////////////////////////////////////////////////


                } else if(theme == 2) {
                    intent.setClass(MainMonthActivity.this, ShowNoSmokingActivity.class);
                    intent.putExtra("theme", 2);
                } else if(theme == 3) {
                    intent.setClass(MainMonthActivity.this, ShowDietActivity.class);
                    intent.putExtra("theme", 3);
                }
                startActivity(intent);
                finish();
            }
        });

        // 세팅 버튼 클릭 시, 세팅 화면으로
        btnMonthSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);
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
            intent.putExtra("activityCheck", 1);

            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_month) {
        } else if (id == R.id.nav_day) {
            Intent intent = new Intent(MainMonthActivity.this, MainActivity.class);
            intent.putExtra("activityCheck", 1);
            intent.putExtra("theme", theme);

            startActivity(intent);
            finish();
        } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_password) {
            switcher.setChecked(!switcher.isChecked());
            Snackbar.make(item.getActionView(), (switcher.isChecked()) ? "is checked" : "not checked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (id == R.id.nav_send) {

        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    // 뒤로가기 버튼을 통해서 왔을 때, 새로고침
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        finish();
//        startActivity(getIntent());
//    }
}
