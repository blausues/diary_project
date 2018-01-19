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

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2018-01-17.
 */

public class WriteNoSmokingActivity extends Activity {
    private TextView tvNoSmokingWriteWriteDate, tvNoSmokingWriteStartDate;
    private EditText editNoSmokingWritePromise;
    private CheckBox checkNoSmokingWriteGiveup;
    private Button btnNoSmokingWriteSave;
    private ImageButton btnNoSmokingWriteNow;

    private NoSmokingDBHelper noSmokingHelper;

    private NoSmokingVO noSmokingVO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nosmoking);

        tvNoSmokingWriteWriteDate = findViewById(R.id.tv_nosmoking_write_writedate);
        tvNoSmokingWriteStartDate = findViewById(R.id.tv_nosmoking_write_startdate);
        editNoSmokingWritePromise = findViewById(R.id.edit_nosmoking_write_promise);
        checkNoSmokingWriteGiveup = findViewById(R.id.check_nosmoking_write_giveup);
        btnNoSmokingWriteNow = findViewById(R.id.btn_nosmoking_write_now);
        btnNoSmokingWriteSave = findViewById(R.id.btn_nosmoking_write_save);

        noSmokingHelper = new NoSmokingDBHelper(this);

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

            tvNoSmokingWriteStartDate.setText("금연 시작 "+sdf2.format(startDate)+" D+"+dDay);
        } else {
            // 포기한 뒤 새로 쓰거나, 아예 처음 금연일기를 쓰는 경우
            tvNoSmokingWriteStartDate.setText("금연 오늘부터 시작! D+1");
            noSmokingVO.setStartDate(writeDateStr);
        }
        tvNoSmokingWriteWriteDate.setText(sdf2.format(selectedDate.getDate()));

        btnNoSmokingWriteNow.setOnClickListener(new NowListener(editNoSmokingWritePromise));

        btnNoSmokingWriteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 insert 작업
                if(checkNoSmokingWriteGiveup.isChecked() == false) {
                    noSmokingVO.setGiveUp(0);
                } else {
                    noSmokingVO.setGiveUp(1);
                }
                noSmokingVO.setPromise(editNoSmokingWritePromise.getText().toString());

                noSmokingHelper.insertNoSmoking(noSmokingVO);

                // helper 성공하면
                Intent responseIntent = new Intent(WriteNoSmokingActivity.this, ShowNoSmokingActivity.class);
                responseIntent.putExtra("writeDate", selectedDate);

                startActivity(responseIntent);
            }
        });
    }
}
