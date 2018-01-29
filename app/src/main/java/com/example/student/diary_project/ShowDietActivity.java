package com.example.student.diary_project;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.diary_project.vo.DietVO;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by student on 2018-01-18.
 */

public class ShowDietActivity extends Activity {
    private TextView tvDietWriteDate, tvDietWeight, tvDietMemo;
    private EditText editDietWeight, editDietMemo;
    private ImageView[] ivDietPlusImages = new ImageView[3];
    private ArrayList<String> imagePath = new ArrayList<>();
    private TextView[] tvDietMenus = new TextView[3];
    private TextView[] tvDietKcals = new TextView[3];
    private EditText[] editDietMenus = new EditText[3];
    private EditText[] editDietKcals = new EditText[3];
    private ImageButton btnDietKcalTable, btnDietPicture;
    private Button btnDietSave;
    private View popupView;
    private PopupWindow popupWindow;
    private ImageView ivPopup;
    private ConstraintLayout LayoutDietWhole;

    private DietVO dietVO;

    private DietDBHelper dietDBHelper;

    private String viewDate;
    private String writeDateStr;
    private int theme,dayMonthYearCheck;

    // 쓰기모드:0, 수정모드:1
    private final int REQUEST_CODE = 1;

    // 쓰기모드:0, 수정모드:1, 읽기모드:2
    private int mode = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diet);

        tvDietWriteDate = findViewById(R.id.tv_diet_writedate);
        tvDietWeight = findViewById(R.id.tv_diet_weight);
        editDietWeight = findViewById(R.id.edit_diet_weight);
        tvDietMemo = findViewById(R.id.tv_diet_memo);
        editDietMemo = findViewById(R.id.edit_diet_memo);
        btnDietKcalTable = findViewById(R.id.btn_diet_kcal_table);
        btnDietPicture = findViewById(R.id.btn_diet_picture);
        btnDietSave = findViewById(R.id.btn_diet_save);
        LayoutDietWhole = findViewById(R.id.layout_diet_whole);

        String pkg = getPackageName();
        int tmpID;
        for (int i = 0; i < tvDietMenus.length; i++) {
            tmpID = getResources().getIdentifier("iv_diet_plusImage" + i, "id", pkg);
            ivDietPlusImages[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("tv_diet_menu" + (i + 1), "id", pkg);
            tvDietMenus[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("tv_diet_kcal" + (i + 1), "id", pkg);
            tvDietKcals[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("edit_diet_menu" + (i + 1), "id", pkg);
            editDietMenus[i] = findViewById(tmpID);
            tmpID = getResources().getIdentifier("edit_diet_kcal" + (i + 1), "id", pkg);
            editDietKcals[i] = findViewById(tmpID);

            tvDietMenus[i].setOnClickListener(new tvClickListener());
            tvDietKcals[i].setOnClickListener(new tvClickListener());
        }
        tvDietWeight.setOnClickListener(new tvClickListener());
        tvDietMemo.setOnClickListener(new tvClickListener());

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // 이미지뷰 띄워주는 팝업 inflate
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        int lang_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ViewGroup.LayoutParams.WRAP_CONTENT, dm);
        int lang_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ViewGroup.LayoutParams.WRAP_CONTENT, dm);
        popupView = View.inflate(this, R.layout.popup_window1, null);
        popupWindow = new PopupWindow(popupView, lang_width, lang_height, true);

        ivPopup = popupView.findViewById(R.id.img_kcal_table);

        ivPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        for (int i = 0; i < ivDietPlusImages.length; i++) {
            final int currentIdx = i;
            ivDietPlusImages[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode == 2) {
                        // 클릭 시, 확대
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(imagePath.get(currentIdx)));
                            ivPopup.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (ivDietPlusImages[currentIdx] != null) {
                            popupWindow.showAtLocation(LayoutDietWhole, Gravity.CENTER, 0, 0);
                        }
                    } else {
                        // 클릭 시, 삭제
                        imagePath.remove(currentIdx);
                        for (int j = 0; j < ivDietPlusImages.length; j++) {
                            // imageView를 null로
                            ivDietPlusImages[j].setImageResource(0);

                        }
                        // 저장해둔 path로 imageView 새로 그려주기
                        for (int k = 0; k < imagePath.size(); k++) {
                            if (ivDietPlusImages[k].getDrawable() == null) {
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(imagePath.get(k)));
                                    ivDietPlusImages[k].setImageBitmap(bitmap);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
        }

        btnDietPicture.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivDietPlusImages[2].getDrawable() != null) {
                    Toast.makeText(ShowDietActivity.this, "사진은 세장만^^", Toast.LENGTH_SHORT).show();
                } else {
                    YPhotoPickerIntent intent = new YPhotoPickerIntent(ShowDietActivity.this);
                    intent.setMaxSelectCount(3);
                    intent.setShowCamera(true);
                    intent.setShowGif(true);
                    intent.setSelectCheckBox(true);
                    intent.setMaxGrideItemCount(3);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////
        dietDBHelper = new DietDBHelper(this);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy년 MM월 dd일");

        Intent intent = getIntent();
        ///////////////////////////////////////////////////////////////////////////////
        //테마1 인텐트값 작성
        writeDateStr = intent.getStringExtra("selectedDate");
        viewDate = intent.getStringExtra("viewDate");
        dayMonthYearCheck = intent.getIntExtra("dayMonthYearCheck",0);
        theme = intent.getIntExtra("theme",0);
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        Date writeDate = sdf.parse(writeDateStr, new ParsePosition(0));

        dietVO = dietDBHelper.selectDietDate(writeDateStr);
        if (dietVO.getWriteDate() == null) {
            mode = 0;
            dietVO.setWriteDate(writeDateStr);
        } else {
            mode = 2;
        }
        tvDietWriteDate.setText(sdf2.format(writeDate));

        if (mode == 0) {
            for (int i = 0; i < tvDietMenus.length; i++) {
                tvDietMenus[i].setVisibility(View.GONE);
                tvDietKcals[i].setVisibility(View.GONE);
                editDietMenus[i].setVisibility(View.VISIBLE);
                editDietKcals[i].setVisibility(View.VISIBLE);
            }
            tvDietWeight.setVisibility(View.GONE);
            tvDietMemo.setVisibility(View.GONE);

            editDietWeight.setVisibility(View.VISIBLE);
            editDietMemo.setVisibility(View.VISIBLE);

            btnDietSave.setVisibility(View.VISIBLE);
            btnDietKcalTable.setVisibility(View.VISIBLE);
            btnDietPicture.setVisibility(View.VISIBLE);
        } else if (mode == 2) {
            for (int i = 0; i < tvDietMenus.length; i++) {
                tvDietMenus[i].setText(dietVO.getMenu(i));
                tvDietKcals[i].setText(dietVO.getKcal(i) + "");
            }
            tvDietWeight.setText(dietVO.getWeight() + "");
            tvDietMemo.setText(dietVO.getMemo());

            imagePath = dietVO.getPhoto();

            for (int i = 0; i < imagePath.size(); i++) {
                if (ivDietPlusImages[i].getDrawable() == null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(imagePath.get(i)));
                        ivDietPlusImages[i].setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        btnDietKcalTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = layoutInflater.inflate(R.layout.popup_window1, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                popupWindow.showAsDropDown(btnDietKcalTable, 60, -50);
                popupWindow.showAtLocation(LayoutDietWhole, Gravity.CENTER, 0, 0);

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
                InputMethodManager imm = (InputMethodManager) getSystemService(ShowNoSmokingActivity.INPUT_METHOD_SERVICE);

                dietVO.setPhoto(imagePath);

                for (int i = 0; i < editDietMenus.length; i++) {
                    dietVO.setMenu(i, editDietMenus[i].getText().toString());
                    if (editDietKcals[i].getText().length() != 0 && editDietKcals[i].getText() != null) {
                        dietVO.setKcal(i, Float.parseFloat(editDietKcals[i].getText().toString()));
                    }
                }
                if (editDietWeight.getText().length() != 0 && editDietWeight.getText() != null) {
                    dietVO.setWeight(Float.parseFloat(editDietWeight.getText().toString()));
                }
                dietVO.setMemo(editDietMemo.getText().toString());

                int result = 0;

                if (mode == 0) {
                    // insert
                    result = dietDBHelper.insertDiet(dietVO);
//                    mode = 1;
                } else if (mode == 1) {
                    // update
                    result = dietDBHelper.updateDiet(dietVO);
                }

                if (result > 0) {
                    for (int i = 0; i < tvDietMenus.length; i++) {
                        tvDietMenus[i].setText(editDietMenus[i].getText());
                        tvDietKcals[i].setText(editDietKcals[i].getText());

                        tvDietMenus[i].setVisibility(View.VISIBLE);
                        tvDietKcals[i].setVisibility(View.VISIBLE);
                        editDietMenus[i].setVisibility(View.GONE);
                        editDietKcals[i].setVisibility(View.GONE);
                    }
                    tvDietWeight.setText(editDietWeight.getText());
                    tvDietMemo.setText(editDietMemo.getText());

                    tvDietWeight.setVisibility(View.VISIBLE);
                    tvDietMemo.setVisibility(View.VISIBLE);
                    editDietWeight.setVisibility(View.GONE);
                    editDietMemo.setVisibility(View.GONE);

                    btnDietSave.setVisibility(View.GONE);
                    btnDietKcalTable.setVisibility(View.GONE);
                    btnDietPicture.setVisibility(View.GONE);

                    imm.hideSoftInputFromWindow(editDietMemo.getWindowToken(), 0);

                    // 읽기 모드로 전환
                    mode = 2;
                } else {
                    Toast.makeText(ShowDietActivity.this, "에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class tvClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // 수정 모드로 전환
            mode = 1;

            // tv 클릭 시, edit 나타내기
            for (int i = 0; i < tvDietMenus.length; i++) {
                editDietMenus[i].setText(tvDietMenus[i].getText());
                editDietKcals[i].setText(tvDietKcals[i].getText());

                tvDietMenus[i].setVisibility(View.GONE);
                tvDietKcals[i].setVisibility(View.GONE);
                editDietMenus[i].setVisibility(View.VISIBLE);
                editDietKcals[i].setVisibility(View.VISIBLE);
            }
            editDietWeight.setText(tvDietWeight.getText());
            editDietMemo.setText(tvDietMemo.getText());

            tvDietWeight.setVisibility(View.GONE);
            tvDietMemo.setVisibility(View.GONE);
            editDietWeight.setVisibility(View.VISIBLE);
            editDietMemo.setVisibility(View.VISIBLE);

            InputMethodManager imm = (InputMethodManager) getSystemService(ShowNoSmokingActivity.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, 0);

            // 음...포커스 어케 주지 ㅠㅠ click되는 view는 tv인데
            editDietWeight.requestFocus();

            btnDietSave.setVisibility(View.VISIBLE);
            btnDietKcalTable.setVisibility(View.VISIBLE);
            btnDietPicture.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> addPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                if(addPhotos.size() + imagePath.size() < 4){
                    for (int a = 0; a < addPhotos.size(); a++) {
                        imagePath.add(getIndex(), addPhotos.get(a));
                    }
                    for (int i = getIndex(); i < imagePath.size(); i++) {
                        if (ivDietPlusImages[i].getDrawable() == null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(imagePath.get(i)));
                                ivDietPlusImages[i].setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "사진은 세 장만^^", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Uri getUriFromPath(String path) { // 사진path를 uri 로 변환시키는 메소드.
        String fileName = path;
        Uri fileUri = Uri.parse(fileName);
        String filePath = fileUri.getPath();
        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                "_data = '" + filePath + "'", null, null);
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        return uri;
    }

    private int getIndex() { // 이미지뷰리스트 인덱스값 뽑아오는 메소드
        int result = 0;
        for (int i = 0; i < ivDietPlusImages.length; i++) {
            if (ivDietPlusImages[i].getDrawable() == null) {
                result = i;
                break;
            }
        }
        return result;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //테마1 뒤로 가기
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(ShowDietActivity.this,MainActivity.class);
//        intent.putExtra("selectedDate",writeDateStr);
//        intent.putExtra("viewDate",viewDate);
//        intent.putExtra("dayMonthYearCheck",dayMonthYearCheck);
//        intent.putExtra("theme",theme);
//        startActivity(intent);
//        finish();
//        super.onBackPressed();
//    }
}
