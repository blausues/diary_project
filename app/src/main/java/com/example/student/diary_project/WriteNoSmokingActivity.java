package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2018-01-17.
 */

public class WriteNoSmokingActivity extends Activity {
    private TextView tvNoSmokingWriteDate, tvNoSmokingStartDate, tvNoSmokingPromise;
    private EditText editNoSmokingPromise;
    private CheckBox checkNoSmokingGiveup;
    private Button btnNoSmokingSave;
    private ImageButton btnNoSmokingNow;

    private NoSmokingDBHelper noSmokingHelper;

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

        noSmokingHelper = new NoSmokingDBHelper(this);

        tvNoSmokingPromise.setVisibility(View.GONE);
        editNoSmokingPromise.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        final CalendarDay selectedDate = intent.getParcelableExtra("selectedDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");
        String writeDateStr = sdf.format(selectedDate.getDate());

        noSmokingVO = noSmokingHelper.selectNoSmokingBeforeDate(writeDateStr);
        noSmokingVO.setWriteDate(writeDateStr);

        if(noSmokingVO.getGiveUp() == 0 && noSmokingVO.getStartDate() != null) {
            // 진행중
            Date startDate = sdf.parse(noSmokingVO.getStartDate(), new ParsePosition(0));
            int dDay = (int) Math.floor((selectedDate.getDate().getTime() - startDate.getTime()) / 86400000) + 1;

            tvNoSmokingStartDate.setText("금연 시작 "+sdf2.format(startDate)+" D+"+dDay);
        } else {
            // 포기한 뒤 새로 쓰거나, 아예 처음 금연일기를 쓰는 경우
            tvNoSmokingStartDate.setText("금연 오늘부터 시작! D+1");
            noSmokingVO.setStartDate(writeDateStr);
        }
        tvNoSmokingWriteDate.setText(sdf2.format(selectedDate.getDate()));

        btnNoSmokingNow.setOnClickListener(new NowListener(editNoSmokingPromise));

        btnNoSmokingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 insert 작업
                if(checkNoSmokingGiveup.isChecked() == false) {
                    noSmokingVO.setGiveUp(0);
                } else {
                    noSmokingVO.setGiveUp(1);
                }
                noSmokingVO.setPromise(editNoSmokingPromise.getText().toString());

                int result = noSmokingHelper.insertNoSmoking(noSmokingVO);

                if(result > 0) {
                    // insert 성공
                    tvNoSmokingPromise.setVisibility(View.VISIBLE);
                    editNoSmokingPromise.setVisibility(View.GONE);
                } else {
                    // insert 실패
                    Toast.makeText(WriteNoSmokingActivity.this, "에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
