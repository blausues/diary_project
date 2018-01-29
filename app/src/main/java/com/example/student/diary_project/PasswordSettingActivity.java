package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordSettingActivity extends Activity{
    private TextView tvPasswordSetting;
    private EditText etPasswordSetting;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting);

        tvPasswordSetting = findViewById(R.id.tv_password_setting);
        etPasswordSetting = findViewById(R.id.et_password_setting);

        if(etPasswordSetting.getText().length()==4){
            Intent intent = new Intent(PasswordSettingActivity.this,PasswordCheckActivity.class);
            intent.putExtra("password",etPasswordSetting.getText());
            startActivity(intent);
        }
    }
}
