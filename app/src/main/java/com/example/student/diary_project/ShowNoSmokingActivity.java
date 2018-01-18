package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by student on 2018-01-16.
 */

public class ShowNoSmokingActivity extends Activity {
    private TextView tvNoSmokingWriteDate, tvNoSmokingStartDate, tvNoSmokingPromise;
    private EditText editNoSmokingPromise;
    private CheckBox checkNoSmokingGiveup;
    private Button btnNoSmokingSave;
    private ImageButton btnNoSmokingNow;

    private DiaryDBHelper helper;

    private NoSmokingVO noSmokingVO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_nosmoking);

        tvNoSmokingWriteDate = findViewById(R.id.tv_nosmoking_writedate);
        tvNoSmokingStartDate = findViewById(R.id.tv_nosmoking_startdate);
        tvNoSmokingPromise = findViewById(R.id.tv_nosmoking_promise);
        editNoSmokingPromise = findViewById(R.id.edit_nosmoking_promise);
        checkNoSmokingGiveup = findViewById(R.id.check_nosmoking_giveup);
        btnNoSmokingNow = findViewById(R.id.btn_nosmoking_now);
        btnNoSmokingSave = findViewById(R.id.btn_nosmoking_save);

        helper = new DiaryDBHelper(this);

        Intent intent = getIntent();
        CalendarDay writeDate = intent.getParcelableExtra("writeDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String writeDateStr = sdf.format(writeDate.getDate());

        noSmokingVO = helper.selectNoSmokingDate(writeDateStr);

        Date startDate = sdf.parse(noSmokingVO.getStartDate(), new ParsePosition(0));

        int dDay = (int) Math.floor((writeDate.getDate().getTime() - startDate.getTime()) / 86400000) + 1;

        CalendarDay startDay = CalendarDay.from(startDate);

        tvNoSmokingWriteDate.setText(writeDate.getYear()+"년 "+writeDate.getMonth()+1+"월 "+writeDate.getDay()+"일");
        tvNoSmokingStartDate.setText("금연 시작 "+startDay.getYear()+"년 "+startDay.getMonth()+1+"월 "+startDay.getDay()+"일 "+"D+"+dDay);
        tvNoSmokingPromise.setText(noSmokingVO.getPromise());

        if(noSmokingVO.getGiveUp() == 0) {
            checkNoSmokingGiveup.setChecked(false);
        } else if(noSmokingVO.getGiveUp() == 1) {
            checkNoSmokingGiveup.setChecked(true);
        }

        checkNoSmokingGiveup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNoSmokingSave.setVisibility(View.VISIBLE);

                // 다이얼로그 하나 넣자!!!!!!!!
            }
        });

        // 클릭 시, 편집
        tvNoSmokingPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoSmokingPromise.setVisibility(View.GONE);

                editNoSmokingPromise.setText(tvNoSmokingPromise.getText());
                editNoSmokingPromise.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(ShowNoSmokingActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0,0);

                editNoSmokingPromise.requestFocus();

                // 커서 위치 조정
                editNoSmokingPromise.setSelection(editNoSmokingPromise.getText().length());

                btnNoSmokingSave.setVisibility(View.VISIBLE);
                btnNoSmokingNow.setVisibility(View.VISIBLE);
            }
        });

        btnNoSmokingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 저장
                if(checkNoSmokingGiveup.isChecked() == false) {
                    noSmokingVO.setGiveUp(0);
                } else {
                    noSmokingVO.setGiveUp(1);
                }

                noSmokingVO.setPromise(editNoSmokingPromise.getText().toString());

                helper.updateNoSmoking(noSmokingVO);

                tvNoSmokingPromise.setVisibility(View.VISIBLE);

                tvNoSmokingPromise.setText(editNoSmokingPromise.getText().toString());
                editNoSmokingPromise.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getSystemService(ShowNoSmokingActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editNoSmokingPromise.getWindowToken(), 0);

                btnNoSmokingSave.setVisibility(View.GONE);
                btnNoSmokingNow.setVisibility(View.GONE);
            }
        });

        btnNoSmokingNow.setOnClickListener(new WriteNowListener(editNoSmokingPromise));
    }

    // BACK 버튼 눌렀을 때, SAVE 버튼 안보이게 할까? 냅둬도 될 것 같네
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK) {
//            btnNoSmokingSave.setVisibility(View.GONE);
//        }
//        return false;
//    }
}
