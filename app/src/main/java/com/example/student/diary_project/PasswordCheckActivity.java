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
import android.widget.Toast;

/**
 * Created by student on 2018-01-29.
 */

public class PasswordCheckActivity extends Activity {
    private EditText etPasswordCheck;
    private String password;
    private String viewDate, writeDateStr;
    private int theme, dayMonthYearCheck, activityCheck;

    private PwdDBHelper pwdDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_check);

        etPasswordCheck = findViewById(R.id.et_password_check);

        pwdDBHelper = new PwdDBHelper(this);

        Intent receiveIntent = getIntent();
        password = receiveIntent.getStringExtra("password");
        writeDateStr = receiveIntent.getStringExtra("selectedDate");
        viewDate = receiveIntent.getStringExtra("viewDate");
        dayMonthYearCheck = receiveIntent.getIntExtra("dayMonthYearCheck",0);
        theme = receiveIntent.getIntExtra("theme",0);
        activityCheck = receiveIntent.getIntExtra("activityCheck",0);
        Log.d("passcheck",password);

        etPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(etPasswordCheck.getText().length()==4){
                    if(etPasswordCheck.getText().toString().equals(password)){
                        Toast.makeText(PasswordCheckActivity.this, "비밀번호가 일치합니다.", Toast.LENGTH_SHORT).show();
                        pwdDBHelper.insertPwd(Integer.parseInt(password));

                        Intent intent;
                        if (activityCheck == 0) {
                            intent = new Intent(PasswordCheckActivity.this, MainActivity.class);
                        } else {
                            intent = new Intent(PasswordCheckActivity.this, MainMonthActivity.class);
                        }
                        intent.putExtra("selectedDate", writeDateStr);
                        intent.putExtra("viewDate", viewDate);
                        intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
                        intent.putExtra("theme", theme);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(PasswordCheckActivity.this, "비밀번호가 일치하지 않습니다. 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                        etPasswordCheck.setText("");
                    }
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        etPasswordCheck = findViewById(R.id.et_password_check);
        etPasswordCheck.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                manager.showSoftInput(etPasswordCheck, 0);

            }
        },100);
    }
}
