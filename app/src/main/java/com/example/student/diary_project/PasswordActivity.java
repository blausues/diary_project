package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordActivity extends Activity {
    private EditText etPassword;

    private PwdDBHelper pwdDBHelper;

    private String viewDate, writeDateStr;
    private int theme, dayMonthYearCheck, activityCheck, delete, pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        etPassword = findViewById(R.id.et_password);

        pwdDBHelper = new PwdDBHelper(this);

        Intent receiveIntent = getIntent();
        delete = receiveIntent.getIntExtra("delete", 0);
        writeDateStr = receiveIntent.getStringExtra("selectedDate");
        viewDate = receiveIntent.getStringExtra("viewDate");
        dayMonthYearCheck = receiveIntent.getIntExtra("dayMonthYearCheck",0);
        theme = receiveIntent.getIntExtra("theme",0);
        activityCheck = receiveIntent.getIntExtra("activityCheck",0);

        pwd = pwdDBHelper.selectPwd();

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etPassword.getText().length()==4){
                    if(Integer.parseInt(etPassword.getText().toString()) == pwd) {
                        Intent intent = new Intent();
                        if(delete == 0) {
                            // splash -> password
                            intent.setClass(PasswordActivity.this, MainMonthActivity.class);
                        } else if(delete == 1) {
                            // 비밀번호 해제
                            pwdDBHelper.deletePwd();
                            if (activityCheck == 0) {
                                intent = new Intent(PasswordActivity.this, MainActivity.class);
                            } else {
                                intent = new Intent(PasswordActivity.this, MainMonthActivity.class);
                            }
                        }
                        intent.putExtra("selectedDate", writeDateStr);
                        intent.putExtra("viewDate", viewDate);
                        intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
                        intent.putExtra("theme", theme);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(PasswordActivity.this, "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        etPassword.setText("");
                    }
                }
            }
        });


    }

    @Override // 키보드 자동실행 메소드   onCreate 시점에서는 activity를 만들고 있는 상황이라 onResume시점에 딜레이를 넣어서 요청해야함.
    protected void onResume() {
        super.onResume();

        etPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                manager.showSoftInput(etPassword, 0);

            }
        },100);
    }
}
