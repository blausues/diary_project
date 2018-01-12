package com.example.student.diary_project;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private TextView viewDate;
    private ImageButton btnCalendar, btnThema, btnWrite, btnSetting;
    private Button btnCalendarDay, btnCalendarMonth, btnCalendarYear;
    private Button btnMonthDate,btnYearDate;
    private ImageButton btnMonthLeft, btnMonthRight;
    private ImageButton btnYearLeft, btnYearRight;
    private MaterialCalendarView materialCalendarView;
    private String year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        viewDate = findViewById(R.id.tv_date);
        btnCalendar = findViewById(R.id.btn_calendar);
        btnThema = findViewById(R.id.btn_thema);
        btnWrite = findViewById(R.id.btn_write);
        btnSetting = findViewById(R.id.btn_setting);

        SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthSdf = new SimpleDateFormat("MM");

        year = yearSdf.format(new Date());
        month = monthSdf.format(new Date());

        viewDate.setText(year + "." + month);

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

                                SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
                                SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
                                SimpleDateFormat daySdf = new SimpleDateFormat("dd");

                                Date mDate = date.getDate();
                                year = yearSdf.format(mDate);
                                month = monthSdf.format(mDate);
                                day = daySdf.format(mDate);

                                viewDate.setText(year + "." + month + "." + day);

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

                        btnMonthDate.setText(year + " . " + month);

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
                                btnMonthDate.setText(year + " . " + month);
                            }
                        });

                        btnMonthDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewDate.setText(year + "." + month);
                                dialogMonth.cancel();
                            }
                        });

                        btnMonthRight.setOnClickListener(new View.OnClickListener() {
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
                                btnMonthDate.setText(year + " . " + month);
                            }
                        });


                        WindowManager.LayoutParams lp1 = new WindowManager.LayoutParams();
                        lp1.copyFrom(dialogMonth.getWindow().getAttributes());
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
                btnCalendarYear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialogYear = new Dialog(v.getContext());
                        dialogYear.setContentView(R.layout.dialog_calendar_year);

                        btnYearLeft = dialogYear.findViewById(R.id.btn_year_left);
                        btnYearDate = dialogYear.findViewById(R.id.btn_year_date);
                        btnYearRight = dialogYear.findViewById(R.id.btn_year_right);

                        SimpleDateFormat yearDialogYear = new SimpleDateFormat("yyyy");

                        year = yearDialogYear.format(new Date());

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
                                viewDate.setText(year);
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
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = 800;
                lp.height = 200;
                dialog.show();
                Window window = dialog.getWindow();
                window.setAttributes(lp);
            }
        });
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
