package com.example.student.diary_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.DrawingVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by student on 2018-01-18.
 */

public class DrawDiaryActivity extends Activity {
    private ConstraintLayout showPaint;
    private ImageButton btnColor, btnEraser, btnPrev, btnClear;
    private Button btnUpdate,btnEdit;
    private EditText drawEdit;
    private GridView drawGridView;
    private TextView tvDate,tvContent;
    private String selectDate,viewDate;
    private int dayMonthYearCheck,theme;
    private int activityCheck;

    private int checkColorMenu = 0;

    private DrawView drawView;
    private String textContent,filename;

    private DrawDBHelper drawDBHelper;
    private DrawingVO drawingVO;

    private Bitmap outputBitmap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_draw);

        //레이아웃 아이디 모음
        btnColor = findViewById(R.id.btn_drawshow_color);
        btnEraser = findViewById(R.id.btn_drawshow_eraser);
        btnPrev = findViewById(R.id.btn_drawshow_prev);
        btnClear = findViewById(R.id.btn_drawshow_clear);
        btnUpdate = findViewById(R.id.btn_drawshow_update);
        drawEdit = findViewById(R.id.drawshow_edit);
        drawGridView = findViewById(R.id.drawshow_gridView);
        drawView = (DrawView) findViewById(R.id.drawshow_view);
        tvDate = findViewById(R.id.tv_drawshow_date);
        showPaint = findViewById(R.id.drawshow_paint);
        tvContent = findViewById(R.id.tv_drawshow_content);
        btnEdit = findViewById(R.id.btn_drawshow_edit);

        Log.d("hh", "sadasdasdasdasdasdasd");
        //////////////////////////////////////////////////////////////////////////////////
        //액티비티 생성
        drawDBHelper = new DrawDBHelper(this);
        drawingVO = new DrawingVO();

        //인텐트 값 가져오기
        Intent intent = getIntent();
        selectDate = intent.getStringExtra("selectedDate");
        viewDate = intent.getStringExtra("viewDate");
        dayMonthYearCheck = intent.getIntExtra("dayMonthYearCheck",0);
        theme = intent.getIntExtra("theme",0);
        activityCheck = intent.getIntExtra("activityCheck",0);

        //db불러오기
        drawingVO = drawDBHelper.selectDrawDiary(selectDate);
        filename = drawingVO.getDrawFileName();
        tvDate.setText(drawingVO.getDrawDate());
        tvContent.setText(drawingVO.getDrawContent());
        drawEdit.setText(tvContent.getText());

        //////////////////////////////////////////////////////////////////////////////////

        //저장한 그림 가져오기
        screenShotOutput(outputBitmap);

        ///////////////////////////////////////////////////////////////////////////

        //읽기 화면에서 수정화면 전환
        drawView.selColor(Color.TRANSPARENT);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPaint.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                keyboardDown();
            }
        });

        tvContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tvContent.setVisibility(View.INVISIBLE);
                drawEdit.setVisibility(View.VISIBLE);
                btnUpdate.setVisibility(View.VISIBLE);

                keyboardUp();
                return false;
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////

        //텍스트에 밑줄 긋기
        textContent = tvContent.getText()+"";

        SpannableString content = new SpannableString(textContent);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvContent.setText(content);


        /////////////////////////////////////////////////////////////////////////////////////

        //그리기 색상 표시 디자인하기 위해 그리드 뷰로 구성
        List<Integer> colorList = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            colorList.add(x);
        }

        DrawGridViewAdapter adapter = new DrawGridViewAdapter(this, R.layout.item_colorbutton, colorList);
        drawGridView.setAdapter(adapter);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        btnColor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (checkColorMenu == 0) {
                    drawGridView.setVisibility(View.VISIBLE);
                    drawGridView.setFocusable(true);
                    drawGridView.setFocusableInTouchMode(true);
                    drawGridView.bringToFront();
                    drawGridView.requestFocus();
                    checkColorMenu = 1;
                } else {
                    drawGridView.setVisibility(View.INVISIBLE);
                    checkColorMenu = 0;
                }

            }
        });

        drawGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        drawView.selColor(Color.RED);
                        break;
                    case 1:
                        drawView.selColor(Color.BLUE);
                        break;
                    case 2:
                        drawView.selColor(Color.rgb(255, 165, 000));
                        break;
                    case 3:
                        drawView.selColor(Color.BLACK);
                        break;
                    case 4:
                        drawView.selColor(Color.GREEN);
                        break;
                }
                drawGridView.setVisibility(View.INVISIBLE);
                checkColorMenu = 0;
                keyboardDown();
            }
        });

        btnEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.eraser(Color.WHITE);
                keyboardDown();
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.printBack();
                keyboardDown();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setClear();
                keyboardDown();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardDown();
                Bitmap myViewBitmap = getBitmapFromView(drawView);
                File result = screenShotSave(myViewBitmap);
                drawingVO.setDrawDate(selectDate);
                drawingVO.setDrawContent(drawEdit.getText()+"");
                drawDBHelper.updateDrawDiary(drawingVO);

                //저장뒤 바로 읽기화면
                Intent intent = new Intent(DrawDiaryActivity.this,DrawDiaryActivity.class);
                intent.putExtra("selectedDate",selectDate);
                intent.putExtra("viewDate",viewDate);
                intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
                intent.putExtra("theme",theme);
                intent.putExtra("activityCheck",activityCheck);
                startActivity(intent);
                finish();
                ///////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        });
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    //그림 파일 저장 및 불러오기
    //저장
    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private File screenShotSave(Bitmap screenBitmap) {

        //String filename = new Random().nextInt(1000) + "screenshot.jpg";
        File root = Environment.getExternalStorageDirectory();
        File file = new File(root.getAbsolutePath() + "/DCIM/Test1/" + filename);
        Toast.makeText(DrawDiaryActivity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            screenBitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    //불러오기
    private void screenShotOutput(Bitmap outputBitmap){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Test1/"+filename;
        BitmapFactory.Options bo = new BitmapFactory.Options();
        outputBitmap = BitmapFactory.decodeFile(path, bo);

        drawView.setBackground(outputBitmap);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //키보드

    //키보드 올리기
    public void keyboardUp(){
        InputMethodManager im = (InputMethodManager)getSystemService(DrawDiaryActivity.INPUT_METHOD_SERVICE);
        im.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
        drawEdit.requestFocus();
    }

    //키보드 내리기
    public void keyboardDown() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(drawEdit.getWindowToken(), 0);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //뒤로 가기
    @Override
    public void onBackPressed() {
        Intent intent;
        if (activityCheck == 0) {
            intent = new Intent(DrawDiaryActivity.this, MainActivity.class);
        } else {
            intent = new Intent(DrawDiaryActivity.this, MainMonthActivity.class);
        }
        intent.putExtra("selectedDate", selectDate);
        intent.putExtra("viewDate", viewDate);
        intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
        intent.putExtra("theme", theme);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
