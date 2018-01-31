package com.example.student.diary_project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by student on 2018-01-29.
 */

public class SplashActivity extends AppCompatActivity {
    private PwdDBHelper pwdDBHelper;
    private int pwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pwdDBHelper = new PwdDBHelper(this);
        pwd = pwdDBHelper.selectPwd();

        new Handler().postDelayed(new Runnable() {
            @Override
              public void run() {
                Intent intent = new Intent();

                if(pwd == 0) {
                    // 패스워드 설정 안했을 시
                    intent.setClass(SplashActivity.this, MainMonthActivity.class);
                } else {
                    // 패스워드 설정 했을 시
                    intent.setClass(SplashActivity.this, PasswordActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
