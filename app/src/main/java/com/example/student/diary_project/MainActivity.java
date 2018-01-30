package com.example.student.diary_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.AllDiaryVO;
import com.example.student.diary_project.vo.DietVO;
import com.example.student.diary_project.vo.DrawingVO;
import com.example.student.diary_project.vo.NoSmokingVO;
import com.example.student.diary_project.vo.NormalVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

// theme 0:일반, 1:그림, 2:금연, 3:다이어트 4:전체
public class MainActivity extends Activity {
    private TextView viewDate;
    private ImageButton btnCalendar, btnThema, btnWrite, btnSetting;
    private Button btnCalendarDay, btnCalendarMonth, btnCalendarYear;
    private Button btnMonthDate, btnYearDate;
    private ImageButton btnMonthLeft, btnMonthRight;
    private ImageButton btnYearLeft, btnYearRight;
    private MaterialCalendarView materialCalendarView;
    private String selectDate;
    private String year, month;
    private String todayDate;
    private SimpleDateFormat currentDate;

    //db액티비티
    private DrawDBHelper drawDBHelper;
    private DietDBHelper dietDBHelper;
    private NoSmokingDBHelper noSmokingDBHelper;
    private WriteNormalDBHelper normalDBHelper;
    //////////////////////////////////////////////////////////////////////////////////////////

    //리스트 생성 위해 필요한것
    private ListView listview;
    private List<NormalVO> normalVOList;
    private List<DrawingVO> drawingVOList;
    private List<NoSmokingVO> noSmokingVOList;
    private List<DietVO> dietVOList;
    private AllDiaryAdapter adapter;
    private List<AllDiaryVO> tmpAllList;
    private int theme = 0;
    private int dayMonthYearCheck = 1; // 0:day 1:month 2:year
    ///////////////////////////////////////////////////////////////////////////////////////////
    private int t0, t1, t2, t3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        //db액티비티 생성
        drawDBHelper = new DrawDBHelper(this);
        dietDBHelper = new DietDBHelper(this);
        normalDBHelper = new WriteNormalDBHelper(this);
        noSmokingDBHelper = new NoSmokingDBHelper(this);

        //레이아웃 아이디 모음

        viewDate = findViewById(R.id.tv_date);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnThema = findViewById(R.id.btn_thema);
        btnWrite = findViewById(R.id.btn_write);
        btnSetting = findViewById(R.id.btn_setting);
        listview = findViewById(R.id.listview_diary);

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //백버튼시 기존 액티비티 화면 유지하며 새로고침 하기위하여
        currentDate = new SimpleDateFormat("yyyy-MM");

        Intent receiveIntent = getIntent();

        dayMonthYearCheck = receiveIntent.getIntExtra("dayMonthYearCheck",1);
        theme = receiveIntent.getIntExtra("theme",0);

        if(theme == 4){
            btnWrite.setVisibility(View.INVISIBLE);
        }

        if(receiveIntent.getStringExtra("selectedDate")==null){
            selectDate = currentDate.format(new Date());

            //처음 화면 리스트 생성
            currentDate = new SimpleDateFormat("MM");
            String currentMonthDate = currentDate.format(new Date());
            month = currentMonthDate;
            monthListCreate();
        }else{
            selectDate = receiveIntent.getStringExtra("viewDate");
            if(dayMonthYearCheck == 0){
                dayListCreate();
            }else if(dayMonthYearCheck == 1){
                month = selectDate.substring(5,7);
                year = selectDate.substring(0,4);
                monthListCreate();
            }else{
                yearListCreate();
            }
        }
        viewDate.setText(selectDate);


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //쓰기 눌렀을때 현재 날짜로 바로 가게
        currentDate = new SimpleDateFormat("yyyy-MM-dd");
        todayDate = currentDate.format(new Date());

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //일정 선택 ////////////////////////////////////////////////////////////////////////////////////////////////
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_calendarmenu);

                btnCalendarDay = dialog.findViewById(R.id.btn_calendar_day);
                btnCalendarMonth = dialog.findViewById(R.id.btn_calendar_month);
                btnCalendarYear = dialog.findViewById(R.id.btn_calendar_year);

                //일별
                btnCalendarDay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogDay = new Dialog(v.getContext());
                        dialogDay.setContentView(R.layout.dialog_calendar_day);

                        //start 달력 데코 (주말 색 표시,오늘 날짜 색 표시)
                        materialCalendarView = (MaterialCalendarView) dialogDay.findViewById(R.id.dialog_calendar);
                        materialCalendarView.addDecorators(
                                new CalendarSaturdayDecorate(),
                                new CalendarSundayDecorate(),
                                new CalendarTodayDecorate());
                        //달력 데코 끝

                        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                            @Override
                            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                                currentDate = new SimpleDateFormat("yyyy-MM-dd");

                                Date mDate = date.getDate();
                                selectDate = currentDate.format(mDate);

                                ////////////////////////////////////////////////////////////////////////////////
                                viewDate.setText(selectDate);

                                dayListCreate(); //테마별 일별 리스트
                                dialogDay.cancel();
                            }
                        });
                        dialogDay.show();

                        dialog.cancel();
                    }
                });
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                //월별
                btnCalendarMonth.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogMonth = new Dialog(v.getContext());
                        dialogMonth.setContentView(R.layout.dialog_calendar_month);

                        btnMonthLeft = dialogMonth.findViewById(R.id.btn_month_left);
                        btnMonthDate = dialogMonth.findViewById(R.id.btn_month_date);
                        btnMonthRight = dialogMonth.findViewById(R.id.btn_month_right);

                        SimpleDateFormat yearDialogMonth = new SimpleDateFormat("yyyy");
                        SimpleDateFormat monthDialogMonth = new SimpleDateFormat("MM");

                        year = yearDialogMonth.format(new Date());
                        month = monthDialogMonth.format(new Date());

                        btnMonthDate.setText(year + "-" + month);

                        btnMonthLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int tMonth = Integer.parseInt(month);
                                int tYear = Integer.parseInt(year);
                                if (tMonth == 1) {
                                    tYear--;
                                    tMonth = 12;
                                } else if (tMonth == 12) {
                                    tMonth = 11;
                                } else {
                                    tMonth = (tMonth) % 12 - 1;
                                }
                                if (tMonth < 10) {
                                    month = "0" + Integer.toString(tMonth);
                                } else {
                                    month = Integer.toString(tMonth);
                                }

                                year = Integer.toString(tYear);
                                btnMonthDate.setText(year + "-" + month);
                            }
                        });

                        btnMonthDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectDate = year + "-" + month;
                                viewDate.setText(selectDate);

                                monthListCreate();
                                /////////////////////////////////////////////////////////////////////////////////////
                                dialogMonth.cancel();
                            }
                        });

                        btnMonthRight.setOnClickListener(new View.OnClickListener()

                        {
                            @Override
                            public void onClick(View v) {
                                int tMonth = Integer.parseInt(month);
                                int tYear = Integer.parseInt(year);
                                if (tMonth == 12) {
                                    tYear++;
                                }
                                tMonth = (tMonth) % 12 + 1;
                                month = Integer.toString(tMonth);
                                year = Integer.toString(tYear);

                                if (tMonth < 10) {
                                    month = "0" + Integer.toString(tMonth);
                                } else {
                                    month = Integer.toString(tMonth);
                                }

                                year = Integer.toString(tYear);
                                btnMonthDate.setText(year + "-" + month);
                            }
                        });


                        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                        lp1.copyFrom(dialogMonth.getWindow().

                                getAttributes());
                        lp1.width = 950;
                        lp1.height = 400;
                        dialogMonth.show();
                        Window window = dialogMonth.getWindow();
                        window.setAttributes(lp1);
                        dialog.cancel();
                    }
                });
                /////////////////////////////////////////////////////////////////////
                //년별
                btnCalendarYear.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogYear = new Dialog(v.getContext());
                        dialogYear.setContentView(R.layout.dialog_calendar_year);

                        btnYearLeft = dialogYear.findViewById(R.id.btn_year_left);
                        btnYearDate = dialogYear.findViewById(R.id.btn_year_date);
                        btnYearRight = dialogYear.findViewById(R.id.btn_year_right);

                        currentDate = new SimpleDateFormat("yyyy");

                        year = currentDate.format(new Date());

                        btnYearDate.setText(year);

                        btnYearLeft.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int tYear = Integer.parseInt(year);
                                tYear--;
                                year = Integer.toString(tYear);
                                btnYearDate.setText(year);
                            }
                        });

                        btnYearDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectDate = year;
                                viewDate.setText(selectDate);

                                yearListCreate(); //테마별 년별 리스트
                                dialogYear.cancel();
                            }
                        });

                        btnYearRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int tYear = Integer.parseInt(year);
                                tYear++;
                                year = Integer.toString(tYear);
                                btnYearDate.setText(year);
                            }
                        });

                        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                        lp2.copyFrom(dialogYear.getWindow().getAttributes());
                        lp2.width = 950;
                        lp2.height = 400;
                        dialogYear.show();
                        Window window = dialogYear.getWindow();
                        window.setAttributes(lp2);
                        dialog.cancel();
                    }
                });

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().

                        getAttributes());
                lp.width = 800;
                lp.height = 200;
                dialog.show();
                Window window = dialog.getWindow();
                window.setAttributes(lp);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if(tmpAllList.get(position).getContent() != null){

                }
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (tmpAllList.get(position).getTheme()) {
                    case 0:
                        Intent normalIntent = new Intent(MainActivity.this,WriteNormalActivity.class);
                        normalIntent.putExtra("selectedDate",tmpAllList.get(position).getWriteDate());
                        normalIntent.putExtra("viewDate",viewDate.getText());
                        normalIntent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                        normalIntent.putExtra("theme",theme);
                        startActivity(normalIntent);
                        finish();
                        break;
                    case 1:
                        if(tmpAllList.get(position).getContent() == null){
                            Intent intent = new Intent(MainActivity.this,WriteDrawDiaryActivity.class);
                            intent.putExtra("selectedDate",tmpAllList.get(position).getWriteDate());
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(MainActivity.this,DrawDiaryActivity.class);
                            intent.putExtra("selectedDate",tmpAllList.get(position).getWriteDate());
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 2:
                        Intent noSmokingIntent = new Intent(MainActivity.this,ShowNoSmokingActivity.class);
                        noSmokingIntent.putExtra("selectedDate",tmpAllList.get(position).getWriteDate());
                        noSmokingIntent.putExtra("viewDate",viewDate.getText());
                        noSmokingIntent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                        noSmokingIntent.putExtra("theme",theme);
                        startActivity(noSmokingIntent);
                        finish();
                        break;
                    case 3:
                        Intent dietIntent = new Intent(MainActivity.this,ShowDietActivity.class);
                        dietIntent.putExtra("selectedDate",tmpAllList.get(position).getWriteDate());
                        dietIntent.putExtra("viewDate",viewDate.getText());
                        dietIntent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                        dietIntent.putExtra("theme",theme);
                        startActivity(dietIntent);
                        finish();
                        break;
                }
            }
        });

        btnThema.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                makeThemeDialog().show();
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                switch (theme) {
                    case 0:
                        if (normalDBHelper.selectNormalDiaryCount(todayDate) >= 1) {
                            Toast.makeText(MainActivity.this, "오늘 작성 완료된 일기.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, WriteNormalActivity.class);
                            intent.putExtra("selectedDate", todayDate);
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 1:
                        if (drawDBHelper.selectDrawDiaryCount(todayDate) >= 1) {
                            Toast.makeText(MainActivity.this, "오늘 작성 완료된 일기.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, WriteDrawDiaryActivity.class);
                            intent.putExtra("selectedDate", todayDate);
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 2:
                        if (noSmokingDBHelper.selectNoSmokingDiaryCount(todayDate) >= 1) {
                            Toast.makeText(MainActivity.this, "오늘 작성 완료된 일기.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, ShowNoSmokingActivity.class);
                            intent.putExtra("selectedDate", todayDate);
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 3:
                        if (dietDBHelper.selectDietDiaryCount(todayDate) >= 1) {
                            Toast.makeText(MainActivity.this, "오늘 작성 완료된 일기.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, ShowDietActivity.class);
                            intent.putExtra("selectedDate", todayDate);
                            intent.putExtra("viewDate",viewDate.getText());
                            intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                            intent.putExtra("theme",theme);
                            startActivity(intent);
                            finish();
                        }
                        break;
                }
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
            }
        });
    }

    static class NoAscCompare implements Comparator<AllDiaryVO> {

        @Override
        public int compare(AllDiaryVO a1, AllDiaryVO a2) {
            return a1.getWriteDate().compareTo(a2.getWriteDate());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //테마별 일별 리스트 생성
    public void dayListCreate() {
        dayMonthYearCheck = 0;

        //리스트 생성
        adapter = new AllDiaryAdapter();
        listview.setAdapter(adapter);

        tmpAllList = new ArrayList<>();
        //테마별 리스트
        switch (theme) {
            case 0:
                if (normalDBHelper.selectNormalDiaryCount(selectDate) == 1) {
                    NormalVO normalVO = normalDBHelper.selectNormalDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                    allDiaryVO.setContent(normalVO.getNormalWriteContent());
                    allDiaryVO.setTheme(normalVO.getTheme());
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addRead(allDiaryVO);
                } else {
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(selectDate);
                    allDiaryVO.setTheme(0);
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addWrite(allDiaryVO);
                }
                break;
            case 1:
                if (drawDBHelper.selectDrawDiaryCount(selectDate) == 1) {
                    DrawingVO drawingVO = drawDBHelper.selectDrawDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                    allDiaryVO.setContent(drawingVO.getDrawContent());
                    allDiaryVO.setTheme(drawingVO.getTheme());
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addRead(allDiaryVO);
                } else {
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(selectDate);
                    allDiaryVO.setTheme(1);
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addWrite(allDiaryVO);
                }
                break;
            case 2:
                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) == 1) {
                    NoSmokingVO noSmokingVO = noSmokingDBHelper.selectNoSmokingDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                    allDiaryVO.setContent(noSmokingVO.getPromise());
                    allDiaryVO.setTheme(noSmokingVO.getTheme());
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addRead(allDiaryVO);
                } else {
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(selectDate);
                    allDiaryVO.setTheme(2);
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addWrite(allDiaryVO);
                }
                break;
            case 3:
                if (dietDBHelper.selectDietDiaryCount(selectDate) == 1) {
                    DietVO dietVO = dietDBHelper.selectDietDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(dietVO.getWriteDate());
                    allDiaryVO.setContent(dietVO.getMemo());
                    allDiaryVO.setTheme(dietVO.getTheme());
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addRead(allDiaryVO);
                } else {
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(selectDate);
                    allDiaryVO.setTheme(3);
                    tmpAllList.add(allDiaryVO);
                    adapter.dataClear();
                    adapter.addWrite(allDiaryVO);
                }
                break;
            case 4:
                //리스트
                tmpAllList = new ArrayList<>();

                for (int dayTheme = 0; dayTheme <= 3; dayTheme++) {
                    AllDiaryVO allDiary = new AllDiaryVO();
                    allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                    allDiary.setWriteDate(selectDate);
                    allDiary.setTheme(dayTheme); // 일반일기 테마
                    tmpAllList.add(allDiary);
                }

                if (normalDBHelper.selectNormalDiaryCount(selectDate) == 1) {
                    NormalVO normalVO = normalDBHelper.selectNormalDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                    allDiaryVO.setContent(normalVO.getNormalWriteContent());
                    allDiaryVO.setTheme(normalVO.getTheme());
                    tmpAllList.set(0, allDiaryVO);
                }

                if (drawDBHelper.selectDrawDiaryCount(selectDate) == 1) {
                    DrawingVO drawingVO = drawDBHelper.selectDrawDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                    allDiaryVO.setContent(drawingVO.getDrawContent());
                    allDiaryVO.setTheme(drawingVO.getTheme());
                    tmpAllList.set(1, allDiaryVO);
                }

                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) == 1) {
                    NoSmokingVO noSmokingVO = noSmokingDBHelper.selectNoSmokingDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                    allDiaryVO.setContent(noSmokingVO.getPromise());
                    allDiaryVO.setTheme(noSmokingVO.getTheme());
                    tmpAllList.set(2, allDiaryVO);
                }

                if (dietDBHelper.selectDietDiaryCount(selectDate) == 1) {
                    DietVO dietVO = dietDBHelper.selectDietDiary(selectDate);
                    AllDiaryVO allDiaryVO = new AllDiaryVO();
                    allDiaryVO.setWriteDate(dietVO.getWriteDate());
                    allDiaryVO.setContent(dietVO.getMemo());
                    allDiaryVO.setTheme(dietVO.getTheme());
                    tmpAllList.set(3, allDiaryVO);
                }

                // 월 데이터 전체 어댑터에 전달하기.
                adapter.dataClear();
                for (AllDiaryVO allDiaryVO : tmpAllList) {
                    adapter.justAdd(allDiaryVO);
                }
                break;
        }
        /////////////////////////////////////////////////////////////////////////////////
    }

    //테마별 월별 리스트 생성
    public void monthListCreate() {
        /////////////////////////////////////////////////////////////////////////////////////////////
        dayMonthYearCheck = 1;

        //리스트 생성
        adapter = new AllDiaryAdapter();
        listview.setAdapter(adapter);

        //테마별 리스트 띄우기

        switch (theme) {
            case 0:
                if (normalDBHelper.selectNormalDiaryCount(selectDate) > 0) {
                    t0 = 1;
                    normalVOList = normalDBHelper.selectNormalDiaryList(selectDate);
                } else {
                    t0 = 0;
                }

                switch (month) {
                    case "01":
                    case "03":
                    case "05":
                    case "07":
                    case "08":
                    case "10":
                    case "12":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(0); // 일반일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t0 == 1) {
                            for (NormalVO normalVO : normalVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                        allDiaryVO.setTheme(normalVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(0); // 일반일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t0 == 1) {
                            for (NormalVO normalVO : normalVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                        allDiaryVO.setTheme(normalVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "02":
                        int iyear = Integer.parseInt(year);
                        if (iyear % 4 == 0) {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(0); // 일반일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t0 == 1) {
                                for (NormalVO normalVO : normalVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                            allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                            allDiaryVO.setTheme(normalVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                        } else {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(0); // 일반일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t0 == 1) {
                                for (NormalVO normalVO : normalVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                            allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                            allDiaryVO.setTheme(normalVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                            break;
                        }
                }
                break;
            //////////////////////////////////////////////////////////////////////////////////////////////////
            case 1:
                if (drawDBHelper.selectDrawDiaryCount(selectDate) > 0) {
                    t1 = 1;
                    drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);
                } else {
                    t1 = 0;
                }

                switch (month) {
                    case "01":
                    case "03":
                    case "05":
                    case "07":
                    case "08":
                    case "10":
                    case "12":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(1); // 그림일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t1 == 1) {
                            for (DrawingVO drawingVO : drawingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                        allDiaryVO.setContent(drawingVO.getDrawContent());
                                        allDiaryVO.setTheme(drawingVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(1); // 그림일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t1 == 1) {
                            for (DrawingVO drawingVO : drawingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                        allDiaryVO.setContent(drawingVO.getDrawContent());
                                        allDiaryVO.setTheme(drawingVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "02":
                        int iyear = Integer.parseInt(year);
                        if (iyear % 4 == 0) {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(1); // 그림일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t1 == 1) {
                                for (DrawingVO drawingVO : drawingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                            allDiaryVO.setContent(drawingVO.getDrawContent());
                                            allDiaryVO.setTheme(drawingVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                        } else {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(1); // 그림일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t1 == 1) {
                                for (DrawingVO drawingVO : drawingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                            allDiaryVO.setContent(drawingVO.getDrawContent());
                                            allDiaryVO.setTheme(drawingVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                            break;
                        }
                }
                break;
            ////////////////////////////////////////////////////////////////////////////////////////////////////
            case 2:
                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) > 0) {
                    t2 = 1;
                    noSmokingVOList = noSmokingDBHelper.selectNoSmokingDiaryList(selectDate);
                } else {
                    t2 = 0;
                }

                switch (month) {
                    case "01":
                    case "03":
                    case "05":
                    case "07":
                    case "08":
                    case "10":
                    case "12":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(2); // 금연일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t2 == 1) {
                            for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                        allDiaryVO.setContent(noSmokingVO.getPromise());
                                        allDiaryVO.setTheme(noSmokingVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(2); // 금연일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t2 == 1) {
                            for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                        allDiaryVO.setContent(noSmokingVO.getPromise());
                                        allDiaryVO.setTheme(noSmokingVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "02":
                        int iyear = Integer.parseInt(year);
                        if (iyear % 4 == 0) {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(2); // 금연일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t2 == 1) {
                                for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                            allDiaryVO.setContent(noSmokingVO.getPromise());
                                            allDiaryVO.setTheme(noSmokingVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                        } else {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(2); // 금연일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t2 == 1) {
                                for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                            allDiaryVO.setContent(noSmokingVO.getPromise());
                                            allDiaryVO.setTheme(noSmokingVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                            break;
                        }
                }
                break;
            ///////////////////////////////////////////////////////////////////////////////////
            case 3:
                if (dietDBHelper.selectDietDiaryCount(selectDate) > 0) {
                    t3 = 1;
                    dietVOList = dietDBHelper.selectDietDiaryList(selectDate);
                } else {
                    t3 = 0;
                }

                switch (month) {
                    case "01":
                    case "03":
                    case "05":
                    case "07":
                    case "08":
                    case "10":
                    case "12":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(3); // 다이어트일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t3 == 1) {
                            for (DietVO dietVO : dietVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                        allDiaryVO.setContent(dietVO.getMemo());
                                        allDiaryVO.setTheme(dietVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(3); // 다이어트일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t3 == 1) {
                            for (DietVO dietVO : dietVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                        allDiaryVO.setContent(dietVO.getMemo());
                                        allDiaryVO.setTheme(dietVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }
                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "02":
                        int iyear = Integer.parseInt(year);
                        if (iyear % 4 == 0) {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(3); // 다이어트일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t3 == 1) {
                                for (DietVO dietVO : dietVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                            allDiaryVO.setContent(dietVO.getMemo());
                                            allDiaryVO.setTheme(dietVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                        } else {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(3); // 다이어트일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t3 == 1) {
                                for (DietVO dietVO : dietVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                            allDiaryVO.setContent(dietVO.getMemo());
                                            allDiaryVO.setTheme(dietVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }
                            // 월 데이터 전체 어댑터에 전달하기.
                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                            break;
                        }
                }
                break;
            /////////////////////////////////////////////////////////////////////////////////
            case 4:
                if (normalDBHelper.selectNormalDiaryCount(selectDate) > 0) {
                    t0 = 1;
                    normalVOList = normalDBHelper.selectNormalDiaryList(selectDate);
                } else {
                    t0 = 0;
                }

                if (drawDBHelper.selectDrawDiaryCount(selectDate) > 0) {
                    t1 = 1;
                    drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);
                } else {
                    t1 = 0;
                }

                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) > 0) {
                    t2 = 1;
                    noSmokingVOList = noSmokingDBHelper.selectNoSmokingDiaryList(selectDate);
                } else {
                    t2 = 0;
                }

                if (dietDBHelper.selectDietDiaryCount(selectDate) > 0) {
                    t3 = 1;
                    dietVOList = dietDBHelper.selectDietDiaryList(selectDate);
                } else {
                    t3 = 0;
                }

                switch (month) {
                    case "01":
                    case "03":
                    case "05":
                    case "07":
                    case "08":
                    case "10":
                    case "12":
                        tmpAllList = new ArrayList<>();

                        ///////////////////////////////////////////////////
                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(0); // 일반일기 테마
                            tmpAllList.add(allDiary);
                        }


                        if (t0 == 1) {
                            for (NormalVO normalVO : normalVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                        allDiaryVO.setTheme(normalVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(1); // 그림일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t1 == 1) {
                            for (DrawingVO drawingVO : drawingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                        allDiaryVO.setContent(drawingVO.getDrawContent());
                                        allDiaryVO.setTheme(drawingVO.getTheme());
                                        tmpAllList.set(i+31, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }


                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(2); // 금연일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t2 == 1) {
                            for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                        allDiaryVO.setContent(noSmokingVO.getPromise());
                                        allDiaryVO.setTheme(noSmokingVO.getTheme());
                                        tmpAllList.set(i+62, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        for (int day = 1; day <= 31; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(3); // 다이어트일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t3 == 1) {
                            for (DietVO dietVO : dietVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                        allDiaryVO.setContent(dietVO.getMemo());
                                        allDiaryVO.setTheme(dietVO.getTheme());
                                        tmpAllList.set(i+93, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        Collections.sort(tmpAllList, new NoAscCompare());

                        // 월 데이터 전체 어댑터에 전달하기.
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "04":
                    case "06":
                    case "09":
                    case "11":
                        tmpAllList = new ArrayList<>();
                        // 빈 일기아이템 채우기
                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(0); // 일반일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t0 == 1) {
                            for (NormalVO normalVO : normalVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                        allDiaryVO.setTheme(normalVO.getTheme());
                                        tmpAllList.set(i, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(1); // 그림일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t1 == 1) {
                            for (DrawingVO drawingVO : drawingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                        allDiaryVO.setContent(drawingVO.getDrawContent());
                                        allDiaryVO.setTheme(drawingVO.getTheme());
                                        tmpAllList.set(i+30, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }


                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(2); // 금연일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t2 == 1) {
                            for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                        allDiaryVO.setContent(noSmokingVO.getPromise());
                                        allDiaryVO.setTheme(noSmokingVO.getTheme());
                                        tmpAllList.set(i+60, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        for (int day = 1; day <= 30; day++) {
                            AllDiaryVO allDiary = new AllDiaryVO();
                            allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                            String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                            allDiary.setWriteDate(date);
                            allDiary.setTheme(3); // 다이어트일기 테마
                            tmpAllList.add(allDiary);
                        }

                        if (t3 == 1) {
                            for (DietVO dietVO : dietVOList) {
                                for (int i = 0; i < tmpAllList.size(); i++) {
                                    if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                        allDiaryVO.setContent(dietVO.getMemo());
                                        allDiaryVO.setTheme(dietVO.getTheme());
                                        tmpAllList.set(i+90, allDiaryVO);
                                        break;
                                    }
                                }
                            }
                        }

                        Collections.sort(tmpAllList, new NoAscCompare());
                        adapter.dataClear();
                        for (AllDiaryVO allDiaryVO : tmpAllList) {
                            adapter.justAdd(allDiaryVO);
                        }
                        break;
                    case "02":
                        int iyear = Integer.parseInt(year);
                        if (iyear % 4 == 0) {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(0); // 일반일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t0 == 1) {
                                for (NormalVO normalVO : normalVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                            allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                            allDiaryVO.setTheme(normalVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(1); // 그림일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t1 == 1) {
                                for (DrawingVO drawingVO : drawingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                            allDiaryVO.setContent(drawingVO.getDrawContent());
                                            allDiaryVO.setTheme(drawingVO.getTheme());
                                            tmpAllList.set(i+29, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(2); // 금연일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t2 == 1) {
                                for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                            allDiaryVO.setContent(noSmokingVO.getPromise());
                                            allDiaryVO.setTheme(noSmokingVO.getTheme());
                                            tmpAllList.set(i+58, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 29; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(3); // 다이어트일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t3 == 1) {
                                for (DietVO dietVO : dietVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                            allDiaryVO.setContent(dietVO.getMemo());
                                            allDiaryVO.setTheme(dietVO.getTheme());
                                            tmpAllList.set(i+87, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            Collections.sort(tmpAllList, new NoAscCompare());

                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                        } else {
                            tmpAllList = new ArrayList<>();
                            // 빈 일기아이템 채우기
                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(0); // 일반일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t0 == 1) {
                                for (NormalVO normalVO : normalVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (normalVO.getNormalWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                                            allDiaryVO.setContent(normalVO.getNormalWriteContent());
                                            allDiaryVO.setTheme(normalVO.getTheme());
                                            tmpAllList.set(i, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(1); // 그림일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t1 == 1) {
                                for (DrawingVO drawingVO : drawingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                            allDiaryVO.setContent(drawingVO.getDrawContent());
                                            allDiaryVO.setTheme(drawingVO.getTheme());
                                            tmpAllList.set(i+28, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(2); // 금연일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t2 == 1) {
                                for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (noSmokingVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                                            allDiaryVO.setContent(noSmokingVO.getPromise());
                                            allDiaryVO.setTheme(noSmokingVO.getTheme());
                                            tmpAllList.set(i+56, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            for (int day = 1; day <= 28; day++) {
                                AllDiaryVO allDiary = new AllDiaryVO();
                                allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                String date = selectDate + "-" + (day / 10) + "" + (day % 10);
                                allDiary.setWriteDate(date);
                                allDiary.setTheme(3); // 다이어트일기 테마
                                tmpAllList.add(allDiary);
                            }

                            if (t3 == 1) {
                                for (DietVO dietVO : dietVOList) {
                                    for (int i = 0; i < tmpAllList.size(); i++) {
                                        if (dietVO.getWriteDate().equals(tmpAllList.get(i).getWriteDate())) {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                                            allDiaryVO.setWriteDate(dietVO.getWriteDate());
                                            allDiaryVO.setContent(dietVO.getMemo());
                                            allDiaryVO.setTheme(dietVO.getTheme());
                                            tmpAllList.set(i+84, allDiaryVO);
                                            break;
                                        }
                                    }
                                }
                            }

                            Collections.sort(tmpAllList, new NoAscCompare());

                            adapter.dataClear();
                            for (AllDiaryVO allDiaryVO : tmpAllList) {
                                adapter.justAdd(allDiaryVO);
                            }
                            break;
                        }
                }
        }

    }


    //테마별 년별 리스트 생성
    public void yearListCreate() {
        dayMonthYearCheck = 2;

        //리스트 생성
        adapter = new AllDiaryAdapter();
        listview.setAdapter(adapter);

        switch (theme) {
            case 0:
                if (normalDBHelper.selectNormalDiaryCount(selectDate) > 0) {
                    normalVOList = normalDBHelper.selectNormalDiaryList(selectDate);

                    tmpAllList = new ArrayList<>();

                    for (NormalVO normalVO : normalVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                        allDiaryVO.setTheme(normalVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }

                    adapter.dataClear();
                    for (AllDiaryVO allDiaryVO : tmpAllList) {
                        adapter.justAdd(allDiaryVO);
                    }
                }
                break;
            case 1:
                if (drawDBHelper.selectDrawDiaryCount(selectDate) > 0) {
                    drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);

                    tmpAllList = new ArrayList<>();

                    for (DrawingVO drawingVO : drawingVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                        allDiaryVO.setContent(drawingVO.getDrawContent());
                        allDiaryVO.setTheme(drawingVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }

                    adapter.dataClear();
                    for (AllDiaryVO allDiaryVO : tmpAllList) {
                        adapter.justAdd(allDiaryVO);
                    }
                }
                break;
            case 2:
                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) > 0) {
                    noSmokingVOList = noSmokingDBHelper.selectNoSmokingDiaryList(selectDate);

                    tmpAllList = new ArrayList<>();

                    for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                        allDiaryVO.setContent(noSmokingVO.getPromise());
                        allDiaryVO.setTheme(noSmokingVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }

                    adapter.dataClear();
                    for (AllDiaryVO allDiaryVO : tmpAllList) {
                        adapter.justAdd(allDiaryVO);
                    }
                }
                break;
            case 3:
                if (dietDBHelper.selectDietDiaryCount(selectDate) > 0) {
                    dietVOList = dietDBHelper.selectDietDiaryList(selectDate);

                    tmpAllList = new ArrayList<>();

                    for (DietVO dietVO : dietVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                        allDiaryVO.setContent(dietVO.getMemo());
                        allDiaryVO.setTheme(dietVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }

                    adapter.dataClear();
                    for (AllDiaryVO allDiaryVO : tmpAllList) {
                        adapter.justAdd(allDiaryVO);
                    }
                }
                break;
            case 4:
                tmpAllList = new ArrayList<>();
                if (normalDBHelper.selectNormalDiaryCount(selectDate) > 0) {
                    normalVOList = normalDBHelper.selectNormalDiaryList(selectDate);

                    for (NormalVO normalVO : normalVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(normalVO.getNormalWriteDate());
                        allDiaryVO.setContent(normalVO.getNormalWriteContent());
                        allDiaryVO.setTheme(normalVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }
                }

                if (drawDBHelper.selectDrawDiaryCount(selectDate) > 0) {
                    drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);

                    for (DrawingVO drawingVO : drawingVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                        allDiaryVO.setContent(drawingVO.getDrawContent());
                        allDiaryVO.setTheme(drawingVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }
                }

                if (noSmokingDBHelper.selectNoSmokingDiaryCount(selectDate) > 0) {
                    noSmokingVOList = noSmokingDBHelper.selectNoSmokingDiaryList(selectDate);

                    for (NoSmokingVO noSmokingVO : noSmokingVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(noSmokingVO.getWriteDate());
                        allDiaryVO.setContent(noSmokingVO.getPromise());
                        allDiaryVO.setTheme(noSmokingVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }
                }

                if (dietDBHelper.selectDietDiaryCount(selectDate) > 0) {
                    dietVOList = dietDBHelper.selectDietDiaryList(selectDate);

                    for (DietVO dietVO : dietVOList) {
                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                        allDiaryVO.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_READ);
                        allDiaryVO.setWriteDate(dietVO.getWriteDate());
                        allDiaryVO.setContent(dietVO.getMemo());
                        allDiaryVO.setTheme(dietVO.getTheme());

                        tmpAllList.add(allDiaryVO);
                    }
                }

                Collections.sort(tmpAllList, new NoAscCompare());
                adapter.dataClear();
                for (AllDiaryVO allDiaryVO : tmpAllList) {
                    adapter.justAdd(allDiaryVO);
                }
                break;
        }
    }

    private Dialog makeThemeDialog() {
        final Dialog themeDialog = new Dialog(this);
        themeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        themeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        themeDialog.getWindow().setGravity(Gravity.BOTTOM);
        themeDialog.setContentView(R.layout.dialog_select_theme_1);

        Button btnThemeNormal = themeDialog.findViewById(R.id.btn_theme1_normal);
        Button btnThemeDraw = themeDialog.findViewById(R.id.btn_theme1_draw);
        Button btnThemeNoSmoking = themeDialog.findViewById(R.id.btn_theme1_nosmoking);
        Button btnThemeDiet = themeDialog.findViewById(R.id.btn_theme1_diet);
        Button btnThemeAll = themeDialog.findViewById(R.id.btn_theme1_all);


        btnThemeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = 0;
                if(dayMonthYearCheck == 0){
                    dayListCreate();
                }else if(dayMonthYearCheck == 1){
                    monthListCreate();
                }else{
                    yearListCreate();
                }
                themeDialog.cancel();
                btnWrite.setVisibility(View.VISIBLE);
            }
        });

        btnThemeDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = 1;
                if(dayMonthYearCheck == 0){
                    dayListCreate();
                }else if(dayMonthYearCheck == 1){
                    monthListCreate();
                }else{
                    yearListCreate();
                }
                themeDialog.cancel();
                btnWrite.setVisibility(View.VISIBLE);
            }
        });

        btnThemeNoSmoking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = 2;
                if(dayMonthYearCheck == 0){
                    dayListCreate();
                }else if(dayMonthYearCheck == 1){
                    monthListCreate();
                }else{
                    yearListCreate();
                }
                themeDialog.cancel();
                btnWrite.setVisibility(View.VISIBLE);
            }
        });

        btnThemeDiet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = 3;
                if(dayMonthYearCheck == 0){
                    dayListCreate();
                }else if(dayMonthYearCheck == 1){
                    monthListCreate();
                }else{
                    yearListCreate();
                }
                themeDialog.cancel();
                btnWrite.setVisibility(View.VISIBLE);
            }
        });

        btnThemeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theme = 4;
                if(dayMonthYearCheck == 0){
                    dayListCreate();
                }else if(dayMonthYearCheck == 1){
                    monthListCreate();
                }else{
                    yearListCreate();
                }
                themeDialog.cancel();
                btnWrite.setVisibility(View.INVISIBLE);
            }
        });
        return  themeDialog;
    }
}