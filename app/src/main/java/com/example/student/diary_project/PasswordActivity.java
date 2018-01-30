package com.example.student.diary_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordActivity extends Activity {
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        etPassword = findViewById(R.id.et_password);
    }

    @Override // 키보드 자동실행 메소드   onCreate 시점에서는 activity를 만들고 있는 상황이라 onResume시점에 딜레이를 넣어서 요청해야함.
    protected void onResume() {
        super.onResume();

        etPassword = findViewById(R.id.et_password_setting);
        etPassword.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                manager.showSoftInput(etPassword, 0);

            }
        },100);
    }
}
