package com.example.student.diary_project;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordActivity extends Activity {
    private TextView tvPassword;
    private EditText etPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        tvPassword = findViewById(R.id.tv_password);
        etPassword = findViewById(R.id.et_password);
    }
}
