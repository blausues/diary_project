package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.example.student.diary_project.vo.NoSmokingVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2018-01-16.
 */

public class NoSmokingActivity extends Activity {
    private TextView tvNoSmokingDate, tvNoSmokingPromise;

    private DiaryDBHelper helper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_nosmoking);

        tvNoSmokingDate = findViewById(R.id.tv_noSmoking_date);
        tvNoSmokingPromise = findViewById(R.id.tv_noSmoking_promise);

        Intent intent = getIntent();
        CalendarDay writeDate = intent.getParcelableExtra("writeDate");

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        String writeDateStr = transFormat.format(writeDate.getDate());

        Log.i("lyh", "CalendarDay : "+writeDate);
        Log.i("lyh", "Date : "+ writeDate.getDate());
        Log.i("lyh", "Str : "+ writeDateStr);

        helper = new DiaryDBHelper(this);

        NoSmokingVO noSmokingVO = helper.selectNoSmokingDate(writeDateStr);

        tvNoSmokingDate.setText(writeDateStr);
        tvNoSmokingPromise.setText(noSmokingVO.getPromise());

        // 클릭 시, 편집
    }
}
