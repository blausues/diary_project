package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordSettingActivity extends Activity {
    private EditText etPasswordSetting;
    private String viewDate, writeDateStr;
    private int theme, dayMonthYearCheck, activityCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_setting);

        Intent receiveIntent = getIntent();
        writeDateStr = receiveIntent.getStringExtra("selectedDate");
        viewDate = receiveIntent.getStringExtra("viewDate");
        dayMonthYearCheck = receiveIntent.getIntExtra("dayMonthYearCheck",0);
        theme = receiveIntent.getIntExtra("theme",0);
        activityCheck = receiveIntent.getIntExtra("activityCheck",0);

        etPasswordSetting = findViewById(R.id.et_password_setting);
        etPasswordSetting.requestFocus();
        etPasswordSetting.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etPasswordSetting.getText().length()==4){
                    Intent intent = new Intent(PasswordSettingActivity.this,PasswordCheckActivity.class);
                    intent.putExtra("password",etPasswordSetting.getText().toString());
                    intent.putExtra("activityCheck", activityCheck);
                    intent.putExtra("selectedDate", writeDateStr);
                    intent.putExtra("viewDate", viewDate);
                    intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
                    intent.putExtra("theme", theme);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        etPasswordSetting = findViewById(R.id.et_password_setting);
        etPasswordSetting.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                manager.showSoftInput(etPasswordSetting, 0);

            }
        }, 100);
    }
}
