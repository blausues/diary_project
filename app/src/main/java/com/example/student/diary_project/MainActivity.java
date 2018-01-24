package com.example.student.diary_project;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.AllDiaryVO;
import com.example.student.diary_project.vo.DrawingVO;
import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


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
    private SimpleDateFormat currentDate;

    private DrawDBHelper drawDBHelper;
    private List<DrawingVO> drawingVOList;

    //리스트 생성 위해 필요한것
    private ListView listview;
    private AllDiaryAdapter adapter;
    private int theme = 1;
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        //db액티비티 생성
        drawDBHelper = new DrawDBHelper(this);

        //레이아웃 아이디 모음

        viewDate = findViewById(R.id.tv_date);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnThema = findViewById(R.id.btn_thema);
        btnWrite = findViewById(R.id.btn_write);
        btnSetting = findViewById(R.id.btn_setting);
        listview = findViewById(R.id.listview_diary);

        //////////////////////////////////////////////////////////////////////////////////////////////////
        //시작시 현재 월
        currentDate = new SimpleDateFormat("yyyy-MM");

        selectDate = currentDate.format(new Date());

        viewDate.setText(selectDate);

        /////////////////////////////////////////////////////////////////////////////////////////////////////
        //첫 시작시 theme=4 현재 월 전체 일기 보여주기

        List<NoSmokingVO> noSmokingVOList;
        drawingVOList = new ArrayList<>();
        drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);


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

                                //리스트 생성
                                adapter = new AllDiaryAdapter();
                                listview.setAdapter(adapter);

                                switch (theme) {
                                    case 0:
                                        break;
                                    case 1:
                                        if (drawDBHelper.selectDrawDiaryCount(selectDate) == 1) {
                                            DrawingVO drawingVO = drawDBHelper.selectDrawDiary(selectDate);
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setWriteDate(drawingVO.getDrawDate());
                                            allDiaryVO.setContent(drawingVO.getDrawContent());
                                            allDiaryVO.setTheme(drawingVO.getTheme());

                                            adapter.addRead(allDiaryVO);
                                        } else {
                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                            allDiaryVO.setWriteDate(selectDate);
                                            allDiaryVO.setTheme(1);

                                            adapter.addWrite(allDiaryVO);
                                        }
                                        break;
                                    case 2:

                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;

                                }
                                /////////////////////////////////////////////////////////////////////////////////
                                viewDate.setText(selectDate);

                                dialogDay.cancel();
                            }
                        });
                        dialogDay.show();

                        dialog.cancel();
                    }
                });
                ///////////////////////////////////////////////////////////////////////
                //월별
                btnCalendarMonth.setOnClickListener(new View.OnClickListener() {
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

                                //리스트 생성
                                List<AllDiaryVO> tmpAllList

                                adapter = new AllDiaryAdapter();
                                listview.setAdapter(adapter);

                                switch (theme) {
                                    case 0:
                                        break;
                                    case 1:
                                        if (drawDBHelper.selectDrawDiaryCount(selectDate) > 0) {

                                            drawingVOList = drawDBHelper.selectDrawDiaryList(selectDate);

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
                                                    for(int day=1; day<=31; day++){
                                                        AllDiaryVO allDiary = new AllDiaryVO();
                                                        allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                                        String date = selectDate+"-"+(day/10)+""+(day%10);
                                                        allDiary.setWriteDate(date);
                                                        allDiary.setTheme(1); // 그림일기 테마
                                                        tmpAllList.add(allDiary);
                                                    }

                                                    // 일기 있는거 대체하기
                                                    for(DrawingVO drawingVO: drawingVOList){
                                                        for(int i=0; i<tmpAllList.size(); i++){
                                                            if(drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())){
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
                                                    // 월 데이터 전체 어댑터에 전달하기.
                                                    adapter.dataClear();
                                                    for(AllDiaryVO allDiaryVO: tmpAllList){
                                                        adapter.justAdd(allDiaryVO);
                                                    }
                                                    break;
                                                case "04":
                                                case "06":
                                                case "09":
                                                case "11":
                                                    tmpAllList = new ArrayList<>();
                                                    // 빈 일기아이템 채우기
                                                    for(int day=1; day<=30; day++){
                                                        AllDiaryVO allDiary = new AllDiaryVO();
                                                        allDiary.setType(AllDiaryAdapter.ITEM_VIEW_TYPE_WRITE);
                                                        String date = selectDate+"-"+(day/10)+""+(day%10);
                                                        allDiary.setWriteDate(date);
                                                        allDiary.setTheme(1); // 그림일기 테마
                                                        tmpAllList.add(allDiary);
                                                    }

                                                    // 일기 있는거 대체하기
                                                    for(DrawingVO drawingVO: drawingVOList){
                                                        for(int i=0; i<tmpAllList.size(); i++){
                                                            if(drawingVO.getDrawDate().equals(tmpAllList.get(i).getWriteDate())){
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
                                                    // 월 데이터 전체 어댑터에 전달하기.
                                                    adapter.dataClear();
                                                    for(AllDiaryVO allDiaryVO: tmpAllList){
                                                        adapter.justAdd(allDiaryVO);
                                                    }
                                                    break;
                                                case "02":
                                                    int iyear = Integer.parseInt(year);
                                                    if (iyear % 4 == 0) {
                                                        for (int x = 1; x <= 29; x++) {
                                                            if (!drawingVOList.get(x - 1).getDrawDate().equals(null)) {
                                                                AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                                allDiaryVO.setWriteDate(drawingVOList.get(x).getDrawDate());
                                                                allDiaryVO.setContent(drawingVOList.get(x).getDrawContent());
                                                                allDiaryVO.setTheme(drawingVOList.get(x).getTheme());

                                                                adapter.addRead(allDiaryVO);
                                                            } else {
                                                                AllDiaryVO allDiaryVO = new AllDiaryVO();

                                                                if (x > 10) {
                                                                    String selDate = selectDate + "-" + x;
                                                                    allDiaryVO.setWriteDate(selDate);
                                                                } else {
                                                                    String selDate = selectDate + "-0" + x;
                                                                    allDiaryVO.setWriteDate(selDate);
                                                                }
                                                                allDiaryVO.setTheme(1);

                                                                adapter.addWrite(allDiaryVO);
                                                            }
                                                        }
                                                    } else {
                                                        for (int x = 1; x <= 28; x++) {
                                                            if (!drawingVOList.get(x - 1).getDrawDate().equals(null)) {
                                                                AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                                allDiaryVO.setWriteDate(drawingVOList.get(x).getDrawDate());
                                                                allDiaryVO.setContent(drawingVOList.get(x).getDrawContent());
                                                                allDiaryVO.setTheme(drawingVOList.get(x).getTheme());

                                                                adapter.addRead(allDiaryVO);
                                                            } else {
                                                                AllDiaryVO allDiaryVO = new AllDiaryVO();

                                                                if (x > 10) {
                                                                    String selDate = selectDate + "-" + x;
                                                                    allDiaryVO.setWriteDate(selDate);
                                                                } else {
                                                                    String selDate = selectDate + "-0" + x;
                                                                    allDiaryVO.setWriteDate(selDate);
                                                                }
                                                                allDiaryVO.setTheme(1);

                                                                adapter.addWrite(allDiaryVO);
                                                            }
                                                        }
                                                    }
                                                    break;
                                            }
                                        } else {
                                            switch (month) {
                                                case "01":
                                                case "03":
                                                case "05":
                                                case "07":
                                                case "08":
                                                case "10":
                                                case "12":
                                                    for (int x = 1; x <= 31; x++) {
                                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                        if (x > 10) {
                                                            String selDate = selectDate + "-" + x;
                                                            allDiaryVO.setWriteDate(selDate);
                                                        } else {
                                                            String selDate = selectDate + "-0" + x;
                                                            allDiaryVO.setWriteDate(selDate);
                                                        }
                                                        allDiaryVO.setTheme(1);

                                                        adapter.addWrite(allDiaryVO);
                                                    }
                                                    break;
                                                case "04":
                                                case "06":
                                                case "09":
                                                case "11":
                                                    for (int x = 1; x <= 30; x++) {
                                                        AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                        if (x > 10) {
                                                            String selDate = selectDate + "-" + x;
                                                            allDiaryVO.setWriteDate(selDate);
                                                        } else {
                                                            String selDate = selectDate + "-0" + x;
                                                            allDiaryVO.setWriteDate(selDate);
                                                        }
                                                        allDiaryVO.setTheme(1);
                                                        adapter.addWrite(allDiaryVO);
                                                    }
                                                    break;
                                                case "02":
                                                    int iyear = Integer.parseInt(year);
                                                    if (iyear % 4 == 0) {
                                                        for (int x = 1; x <= 29; x++) {
                                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                            if (x > 10) {
                                                                String selDate = selectDate + "-" + x;
                                                                allDiaryVO.setWriteDate(selDate);
                                                            } else {
                                                                String selDate = selectDate + "-0" + x;
                                                                allDiaryVO.setWriteDate(selDate);
                                                            }
                                                            allDiaryVO.setTheme(1);

                                                            adapter.addWrite(allDiaryVO);
                                                        }
                                                    } else {
                                                        for (int x = 1; x <= 28; x++) {
                                                            AllDiaryVO allDiaryVO = new AllDiaryVO();
                                                            if (x > 10) {
                                                                String selDate = selectDate + "-" + x;
                                                                allDiaryVO.setWriteDate(selDate);
                                                            } else {
                                                                String selDate = selectDate + "-0" + x;
                                                                allDiaryVO.setWriteDate(selDate);
                                                            }
                                                            allDiaryVO.setTheme(1);

                                                            adapter.addWrite(allDiaryVO);
                                                        }
                                                    }
                                                    break;
                                            }
                                        }
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        break;
                                    case 4:
                                        break;
                                }
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

        btnThema.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (drawDBHelper.selectDrawDiaryCount("2018-01-25") >= 1) {
                    Toast.makeText(MainActivity.this, "이미 작성한 일기입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, WriteDrawDiaryActivity.class);
                    startActivity(intent);
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

}
