package com.example.student.diary_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.student.diary_project.vo.NoSmokingVO;

/**
 * Created by student on 2018-01-17.
 */

public class WriteNoSmokingActivity extends Activity {
    private TextView tvNoSmokingWriteWriteDate, tvNoSmokingWriteStartDate;
    private EditText editNoSmokingWritePromise;
    private CheckBox checkNoSmokingWriteGiveup;
    private Button btnNoSmokingWriteSave;
    private ImageButton btnNoSmokingWriteNow;

    private NoSmokingDBHelper helper;

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

        helper = new NoSmokingDBHelper(this);

        btnNoSmokingWriteNow.setOnClickListener(new WriteNowListener(editNoSmokingWritePromise));

        btnNoSmokingWriteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DB에 insert 작업
            }
        });
    }
}
