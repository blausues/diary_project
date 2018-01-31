package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;

import com.github.paolorotolo.appintro.AppIntro;

/**
 * Created by student on 2018-01-30.
 */

public class ExplainActivity extends AppIntro {
    private ExpainFragment1 fragment1 = new ExpainFragment1();
    private ExpainFragment2 fragment2 = new ExpainFragment2();
    private ExpainFragment3 fragment3 = new ExpainFragment3();

    private String viewDate;
    private String writeDateStr;
    private int theme,dayMonthYearCheck;
    private int activityCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receiveIntent = getIntent();

        writeDateStr = receiveIntent.getStringExtra("selectedDate");
        viewDate = receiveIntent.getStringExtra("viewDate");
        dayMonthYearCheck = receiveIntent.getIntExtra("dayMonthYearCheck",0);
        theme = receiveIntent.getIntExtra("theme",0);
        activityCheck = receiveIntent.getIntExtra("activityCheck",0);

        addSlide(fragment1);
        addSlide(fragment2);
        addSlide(fragment3);

        setBarColor(Color.parseColor("#FFD8D8"));
        setSeparatorColor(Color.parseColor("#FFB2F5"));
        showSkipButton(true);
        setProgressButtonEnabled(true);
        setVibrate(true);
        setVibrateIntensity(30);
        setFadeAnimation();
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        goActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        goActivity();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

    public void goActivity() {
        Intent intent;
        if (activityCheck == 0) {
            intent = new Intent(ExplainActivity.this, MainActivity.class);
        } else {
            intent = new Intent(ExplainActivity.this, MainMonthActivity.class);
        }
        intent.putExtra("selectedDate", writeDateStr);
        intent.putExtra("viewDate", viewDate);
        intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
        intent.putExtra("theme", theme);
        startActivity(intent);
        finish();
    }
}
