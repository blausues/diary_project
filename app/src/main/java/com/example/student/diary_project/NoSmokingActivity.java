package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by student on 2018-01-16.
 */

public class NoSmokingActivity extends Activity {
    private TextView tvNoSmokingWriteDate, tvNoSmokingStartDate, tvNoSmokingPromise;
    private EditText editNoSmokingPromise;
    private Button btnNoSmokingSave;
    private ImageButton btnNoSmokingNow;

    private DiaryDBHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_nosmoking);

        tvNoSmokingWriteDate = findViewById(R.id.tv_nosmoking_writedate);
        tvNoSmokingStartDate = findViewById(R.id.tv_nosmoking_startdate);
        tvNoSmokingPromise = findViewById(R.id.tv_nosmoking_promise);
        editNoSmokingPromise = findViewById(R.id.edit_nosmoking_promise);
        btnNoSmokingSave = findViewById(R.id.btn_nosmoking_save);
        btnNoSmokingNow = findViewById(R.id.btn_nosmoking_now);

        Intent intent = getIntent();
        CalendarDay writeDate = intent.getParcelableExtra("writeDate");

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String writeDateStr = transFormat.format(writeDate.getDate());

        Log.i("lyh", "CalendarDay : "+writeDate);
        Log.i("lyh", "Date : "+ writeDate.getDate());
        Log.i("lyh", "Str : "+ writeDateStr);

        helper = new DiaryDBHelper(this);

        NoSmokingVO noSmokingVO = helper.selectNoSmokingDate(writeDateStr);

        int dDay = (int) Math.floor((noSmokingVO.getWriteDate().getTime() - noSmokingVO.getStartDate().getTime()) / 86400000) + 1;

        CalendarDay startDay = CalendarDay.from(noSmokingVO.getStartDate());

        tvNoSmokingWriteDate.setText(writeDate.getYear()+"년 "+writeDate.getMonth()+1+"월 "+writeDate.getDay()+"일");
        tvNoSmokingStartDate.setText("금연 시작 "+startDay.getYear()+"년 "+startDay.getMonth()+1+"월 "+startDay.getDay()+"일 "+"D+"+dDay);
        tvNoSmokingPromise.setText(noSmokingVO.getPromise());

        // 클릭 시, 편집
        tvNoSmokingPromise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvNoSmokingPromise.setVisibility(View.GONE);

                editNoSmokingPromise.setText(tvNoSmokingPromise.getText());
                editNoSmokingPromise.setVisibility(View.VISIBLE);

                InputMethodManager imm = (InputMethodManager) getSystemService(NoSmokingActivity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0,0);

                editNoSmokingPromise.requestFocus();

                btnNoSmokingSave.setVisibility(View.VISIBLE);
                btnNoSmokingNow.setVisibility(View.VISIBLE);
            }
        });

        btnNoSmokingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 저장

                tvNoSmokingPromise.setVisibility(View.VISIBLE);

                tvNoSmokingPromise.setText(editNoSmokingPromise.getText().toString());
                editNoSmokingPromise.setVisibility(View.GONE);

                InputMethodManager imm = (InputMethodManager) getSystemService(NoSmokingActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editNoSmokingPromise.getWindowToken(), 0);

                btnNoSmokingSave.setVisibility(View.GONE);
                btnNoSmokingNow.setVisibility(View.GONE);
            }
        });

        btnNoSmokingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date today = new Date();
                Locale locale = Locale.US;
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", locale);
                String now = sdf.format(today);
                editNoSmokingPromise.setText(editNoSmokingPromise.getText()+now+" ");
            }
        });
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
