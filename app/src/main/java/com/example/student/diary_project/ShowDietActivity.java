package com.example.student.diary_project;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.DietVO;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.SimpleDateFormat;

/**
 * Created by student on 2018-01-18.
 */

public class ShowDietActivity extends Activity {
    private TextView tvDietWriteDate;
    private EditText editDietWeight, editDietMemo;
    private TextView[] tvDietMenus = new TextView[3];
    private TextView[] tvDietKcals = new TextView[3];
    private EditText[] editDietMenus = new EditText[3];
    private EditText[] editDietKcals = new EditText[3];
    private ImageButton btnDietKcalTable;
    private Button btnDietSave;

    private DietVO dietVO;

    private DietDBHelper dietDBHelper;

    // 쓰기모드:0, 수정모드:1
    private int mode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diet);

        tvDietWriteDate = findViewById(R.id.tv_diet_writedate);
        editDietWeight = findViewById(R.id.edit_diet_weight);
        editDietMemo = findViewById(R.id.edit_diet_memo);
        btnDietKcalTable = findViewById(R.id.btn_diet_kcal_table);
        btnDietSave = findViewById(R.id.btn_diet_save);

        String pkg = getPackageName();
        int tmpID;
        for (int i=0; i<3; i++) {
            tmpID = getResources().getIdentifier("tv_diet_menu"+i, "id", pkg);
            tvDietMenus[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("tv_diet_kcal"+i, "id", pkg);
            tvDietKcals[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("edit_diet_menu"+i, "id", pkg);
            editDietMenus[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("edit_diet_kcal"+i, "id", pkg);
            editDietKcals[i] = findViewById(tmpID);
        }

        dietDBHelper = new DietDBHelper(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");

        Intent intent = getIntent();

        CalendarDay selectedDate = intent.getParcelableExtra("selectedDate");
        String writeDateStr = sdf.format(selectedDate.getDate());

        tvDietWriteDate.setText(sdf2.format(selectedDate.getDate()));

        dietVO = new DietVO();
        dietVO.setWriteDate(writeDateStr);

        btnDietKcalTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_window1, null);
                final PopupWindow popupWindow =  new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.showAsDropDown(btnDietKcalTable, 60, -50);

                ImageView imgKcalTable = popupView.findViewById(R.id.img_kcal_table);
                imgKcalTable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        btnDietSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dietVO.setWeight(Float.parseFloat(editDietWeight.getText().toString()));
                dietVO.setMenu1(editDietMenus[0].getText().toString());
                dietVO.setMenu2(editDietMenus[1].getText().toString());
                dietVO.setMenu3(editDietMenus[2].getText().toString());
                dietVO.setKcal1(Float.parseFloat(editDietKcals[0].getText().toString()));
                dietVO.setKcal2(Float.parseFloat(editDietKcals[1].getText().toString()));
                dietVO.setKcal3(Float.parseFloat(editDietKcals[2].getText().toString()));
                dietVO.setMemo(editDietMemo.getText().toString());

                int result = dietDBHelper.insertDiet(dietVO);

                if(result > 0) {

                } else {
                    Toast.makeText(ShowDietActivity.this, "에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
