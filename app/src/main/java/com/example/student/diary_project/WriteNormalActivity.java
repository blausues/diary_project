package com.example.student.diary_project;

import android.app.Activity;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;
import com.example.student.diary_project.vo.NormalVO;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPagerActivity;
import com.yongbeam.y_photopicker.util.photopicker.PhotoPickerActivity;
import com.yongbeam.y_photopicker.util.photopicker.utils.YPhotoPickerIntent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by student on 2018-01-11.
 */

public class WriteNormalActivity extends Activity {
    private ImageView ivNormalWritePopup;
    private ImageButton selectImage, btnNormalWriteNow, btnNormalWritePopupCancel;
    private Button btnNormalWriteSave;
    private TextView tv_date;
    private EditText etWriteNormal;
    private long now;
    private Date date;
    private SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<ImageView> plusImage = new ArrayList<>();
    private int REQUEST_CODE = 1;
    private int tmpID;
    private ArrayList<String> selectedPhotos;
    private WriteNormalDBHelper normalWriteHelper;
    private NormalVO writeNormalVO;
    private ConstraintLayout writeNormalLayout;
    private RelativeLayout showHideLayout;
    private SoftKeyboard softKeyboard;
    private boolean showHideCheck = false;
    private View pop_View;
    private PopupWindow popupWindow;
    private int mode = 0;

    private String viewDate;
    private String writeDateStr;
    private int theme,dayMonthYearCheck;
    private int activityCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_normal);

        normalWriteHelper = new WriteNormalDBHelper(this);
        tv_date = findViewById(R.id.tv_write_normal_date);
        tv_date.setText(getTime());
        selectImage = findViewById(R.id.ib_select_image);
        etWriteNormal = findViewById(R.id.et_wirte_normal);
        btnNormalWriteNow = findViewById(R.id.btn_normal_write_now);
        btnNormalWriteNow.setOnClickListener(new NowListener(etWriteNormal));
        btnNormalWriteSave = findViewById(R.id.btn_normal_write_save);
        writeNormalLayout = findViewById(R.id.write_normal);
        showHideLayout = findViewById(R.id.write_normal_bottom_panel);

        Log.d("asd", String.valueOf(selectedPhotos));
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        String pkg = getPackageName();
        for (int i = 0; i < 6; i++) {    // 이미지뷰 finViewById 반복문
            tmpID = getResources().getIdentifier("iv_write_normal_plusImage" + i, "id", pkg);
            plusImage.add(i, (ImageView) findViewById(tmpID));
        }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();// DB정보 불러오는부분
        ///////////////////////////////////////////////////////////////////////////////
        //테마1 인텐트값 작성
        writeDateStr = intent.getStringExtra("selectedDate");
        viewDate = intent.getStringExtra("viewDate");
        dayMonthYearCheck = intent.getIntExtra("dayMonthYearCheck",0);
        theme = intent.getIntExtra("theme",0);
        activityCheck = intent.getIntExtra("activityCheck",0);
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        tv_date.setText(writeDateStr);
        etWriteNormal.setText(intent.getStringExtra("content"));
        selectedPhotos = intent.getStringArrayListExtra("imagePath");
        Log.d("asd", String.valueOf(selectedPhotos));
        if (selectedPhotos != null) {
            for (int i = getIndex(); i < selectedPhotos.size(); i++) {
                if (plusImage.get(i).getDrawable() == null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(selectedPhotos.get(i)));
                        plusImage.get(i).setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            selectedPhotos = new ArrayList<>();
            Log.d("asd3", String.valueOf(selectedPhotos));
        }

/////////////////////////////////////////////////////////////////////////////////////////////////////
// 이미지뷰 띄워주는 팝업 inflate
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        int lang_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ViewGroup.LayoutParams.WRAP_CONTENT, dm);
        int lang_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, ViewGroup.LayoutParams.WRAP_CONTENT, dm);
        pop_View = View.inflate(this, R.layout.popup_write_normal_show_image, null);
        popupWindow = new PopupWindow(pop_View, lang_width, lang_height, true);

        ivNormalWritePopup = pop_View.findViewById(R.id.iv_popup_write_normal);
/////////////////////////////////////////////////////////////////////////////////////////////////////
//        키보드 show,hide 이벤트
        InputMethodManager controlManager = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(writeNormalLayout, controlManager);

        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showHideLayout.setVisibility(View.GONE);
                        showHideCheck = false;
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        showHideLayout.setVisibility(View.VISIBLE);
                        showHideCheck = true;
                    }
                });
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////

        for (int i = 0; i < plusImage.size(); i++) { // 이미지뷰 터치시 삭제 or 확대
            final int finalI = i;
            plusImage.get(i).setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (showHideCheck == true) {
                        for (int a = finalI; a < selectedPhotos.size(); a++) {
                            if (plusImage.get(a + 1).getDrawable() != null) {
                                selectedPhotos.remove(a);
                                for (int b = 0; b < 6; b++) {
                                    plusImage.get(b).setImageResource(0);
                                }
                                for (int i = 0; i < selectedPhotos.size(); i++) {
                                    if (plusImage.get(i).getDrawable() == null) {
                                        try {
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(selectedPhotos.get(i)));
                                            plusImage.get(i).setImageBitmap(bitmap);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                break;
                            } else {
                                plusImage.get(a).setImageResource(0);
                                selectedPhotos.remove(a);
                            }
                        }
                    } else {
                        if (null == ivNormalWritePopup) {
                        }
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(selectedPhotos.get(finalI)));
                            ivNormalWritePopup.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (plusImage.get(finalI) != null) {
                            popupWindow.showAtLocation(writeNormalLayout, Gravity.CENTER, 0, 0);
                        } else {

                        }
                    }
                }
            });
        }

        selectImage.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plusImage.get(4).getDrawable() != null) {
                    Toast.makeText(WriteNormalActivity.this, "사진은다섯장만^^", Toast.LENGTH_SHORT).show();
                } else {
                    YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteNormalActivity.this);
                    intent.setMaxSelectCount(5);
                    intent.setShowCamera(true);
                    intent.setShowGif(true);
                    intent.setSelectCheckBox(true);
                    intent.setMaxGrideItemCount(3);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        btnNormalWriteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = normalWriteHelper.selectWriteDate(tv_date.getText().toString());
                if (mode != 1) {
                    writeNormalVO = new NormalVO();
                    writeNormalVO.setNormalWriteDate(tv_date.getText().toString());
                    writeNormalVO.setNormalWriteContent(etWriteNormal.getText().toString());
                    writeNormalVO.setNormalWriteImagePath(selectedPhotos);
                    int result = normalWriteHelper.insertNormal(writeNormalVO);
                    if (result > 0) {
                        Toast.makeText(WriteNormalActivity.this, "쓰기성공", Toast.LENGTH_SHORT).show();
                    } else {
                        // insert 실패
                        Toast.makeText(WriteNormalActivity.this, "읽기에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    writeNormalVO = new NormalVO();
                    writeNormalVO.setNormalWriteDate(tv_date.getText().toString());
                    writeNormalVO.setNormalWriteContent(etWriteNormal.getText().toString());
                    writeNormalVO.setNormalWriteImagePath(selectedPhotos);
                    Log.d("asd", String.valueOf(selectedPhotos));
                    int result = normalWriteHelper.update(writeNormalVO);
                    if (result > 0) {
                        Toast.makeText(WriteNormalActivity.this, "수정성공", Toast.LENGTH_SHORT).show();
                    } else {
                        // insert 실패
                        Toast.makeText(WriteNormalActivity.this, "수정에러가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnNormalWriteSave.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager manager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(etWriteNormal.getWindowToken(),0);
            }
        }, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> addPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                if (selectedPhotos.size() + addPhotos.size() < 6) {
                    for (int a = 0; a < addPhotos.size(); a++) {
                        selectedPhotos.add(getIndex(), addPhotos.get(a));
                    }
                    for (int i = getIndex(); i < selectedPhotos.size(); i++) {
                        if (plusImage.get(i).getDrawable() == null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(selectedPhotos.get(i)));
                                plusImage.get(i).setImageBitmap(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    Toast.makeText(this, "사진은 다섯개만넣으시라구요.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public Uri getUriFromPath(String path) { // 사진path를 uri 로 변환시키는 메소드.
        String fileName = path;
        Uri fileUri = Uri.parse(fileName);
        String filePath = fileUri.getPath();
        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + filePath + "'", null, null);
        c.moveToNext();
        int id = c.getInt(c.getColumnIndex("_id"));
        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
        return uri;
    }

    private int getIndex() { // 이미지뷰리스트 인덱스값 뽑아오는 메소드
        int result = 0;
        for (int i = 0; i < 6; i++) {
            if (plusImage.get(i).getDrawable() == null) {
                result = i;
                break;
            }
        }
        return result;
    }

    private String getTime() {   // 맨 위에 현재 날짜 뽑아 오기.
        now = System.currentTimeMillis();
        date = new Date(now);
        return formate.format(date);
    }

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //뒤로 가기
        @Override
        public void onBackPressed() {
            Intent intent;
            if (activityCheck == 0) {
                intent = new Intent(WriteNormalActivity.this, MainActivity.class);
            } else {
                intent = new Intent(WriteNormalActivity.this, MainMonthActivity.class);
            }
            intent.putExtra("selectedDate", writeDateStr);
            intent.putExtra("viewDate", viewDate);
            intent.putExtra("dayMonthYearCheck", dayMonthYearCheck);
            intent.putExtra("theme", theme);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
}