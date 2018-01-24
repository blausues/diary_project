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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2018-01-16.
 */

public class ShowNoSmokingActivity extends Activity {
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

        Intent intent = getIntent();
        CalendarDay writeDate = intent.getParcelableExtra("writeDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");

        String writeDateStr = sdf.format(writeDate.getDate());

        // 만약에 이 날짜에 쓴게 없으면 write로!!!
        noSmokingVO = noSmokingHelper.selectNoSmokingDate(writeDateStr);

        if(noSmokingVO == null) {
            // insert
            tvNoSmokingPromise.setVisibility(View.GONE);
            editNoSmokingPromise.setVisibility(View.VISIBLE);

            noSmokingVO = noSmokingHelper.selectNoSmokingBeforeDate(writeDateStr);

            if(noSmokingVO.getGiveUp() == 0 && noSmokingVO.getStartDate() != null) {
                // 진행중
                Date startDate = sdf.parse(noSmokingVO.getStartDate(), new ParsePosition(0));
                int dDay = (int) Math.floor((writeDate.getDate().getTime() - startDate.getTime()) / 86400000) + 1;

                tvNoSmokingStartDate.setText("금연 시작 "+sdf2.format(startDate)+" D+"+dDay);
            } else {
                // 포기한 뒤 새로 쓰거나, 아예 처음 금연일기를 쓰는 경우
                tvNoSmokingStartDate.setText("금연 오늘부터 시작! D+1");
                noSmokingVO.setStartDate(writeDateStr);
            }
        } else {
            // update
            Date startDate = sdf.parse(noSmokingVO.getStartDate(), new ParsePosition(0));

            int dDay = (int) Math.floor((writeDate.getDate().getTime() - startDate.getTime()) / 86400000) + 1;

            tvNoSmokingWriteDate.setText(sdf2.format(writeDate.getDate()));
            tvNoSmokingStartDate.setText("금연 시작 "+sdf2.format(startDate)+" D+"+dDay);
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

                    tvNoSmokingPromise.setVisibility(View.GONE);

                    editNoSmokingPromise.setText(tvNoSmokingPromise.getText());
                    editNoSmokingPromise.setVisibility(View.VISIBLE);
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
                    if(noSmokingVO.getGiveUp()==0 && checkNoSmokingGiveup.isChecked()==true) {
                        // 그만하는 것으로 수정
                        noSmokingHelper.updateNoSmokingStartDateGiveUp(noSmokingVO.getWriteDate(), noSmokingVO.getStartDate());
                    } else if(noSmokingVO.getGiveUp()==1 && checkNoSmokingGiveup.isChecked()==false) {
                        // 다시 도전
                        noSmokingHelper.updateNoSmokingStartDateNoGiveUp(noSmokingVO.getWriteDate(), noSmokingVO.getStartDate());
                    }

                    if(checkNoSmokingGiveup.isChecked() == false) {
                        noSmokingVO.setGiveUp(0);
                    } else {
                        noSmokingVO.setGiveUp(1);

                    }
                    noSmokingVO.setPromise(editNoSmokingPromise.getText().toString());

                    int result = noSmokingHelper.updateNoSmoking(noSmokingVO);
                    if(result == 0) {
                        // 수정 실패
                        Toast.makeText(ShowNoSmokingActivity.this, "에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 수정 성공
                        tvNoSmokingPromise.setVisibility(View.VISIBLE);

                        tvNoSmokingPromise.setText(editNoSmokingPromise.getText().toString());
                        editNoSmokingPromise.setVisibility(View.GONE);

                        InputMethodManager imm = (InputMethodManager) getSystemService(ShowNoSmokingActivity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editNoSmokingPromise.getWindowToken(), 0);

                        btnNoSmokingSave.setVisibility(View.GONE);
                        btnNoSmokingNow.setVisibility(View.GONE);
                    }
                }
            });
        }
        btnNoSmokingNow.setOnClickListener(new NowListener(editNoSmokingPromise));
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
