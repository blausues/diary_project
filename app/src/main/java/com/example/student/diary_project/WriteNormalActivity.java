package com.example.student.diary_project;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;
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
    private ImageButton selectImage;
    private TextView tv_date;
    private long now;
    private Date date;
    private SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
    private ArrayList<ImageView> plusImage = new ArrayList<>();
    private int REQUEST_CODE = 1;
    private int tmpID;
    public static ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_normal);

        tv_date = findViewById(R.id.tv_write_normal_date);
        tv_date.setText(getTime());
        selectImage = findViewById(R.id.ib_select_image);

        String pkg = getPackageName();   // findViewById 반복문 돌리기
        for (int i = 0; i < 5; i++) {
            tmpID = getResources().getIdentifier("iv_write_normal_plusImage" + i, "id", pkg);
            plusImage.add(i, (ImageView) findViewById(tmpID));
        }

        for (int i = 0; i < plusImage.size(); i++) {
            final int finalI = i;
            plusImage.get(i).setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int a = finalI; a < selectedPhotos.size(); a++) {
                        Bitmap bitmap = ((BitmapDrawable)plusImage.get(a+1).getDrawable()).getBitmap();
                        plusImage.get(a).setImageResource(0);
                        plusImage.get(a+1).setImageResource(0);
                        plusImage.get(a).setImageBitmap(bitmap);
                        selectedPhotos.remove(a);
                    }
                }
            });
        }
        selectImage.setOnClickListener(new ImageButton.OnClickListener() {
            @Override

            public void onClick(View v) {
                Log.d("check", String.valueOf(selectedPhotos));
                Log.d("check2", String.valueOf(plusImage));
                Log.d("check3", String.valueOf(plusImage.get(0).getDrawable())+"//"+String.valueOf(plusImage.get(0).getDrawable())+"//"+String.valueOf(plusImage.get(1).getDrawable())+"//"+String.valueOf(plusImage.get(2).getDrawable())+"//"+String.valueOf(plusImage.get(3).getDrawable())+"//"+String.valueOf(plusImage.get(4).getDrawable())+"//");
                if (plusImage.get(0).getDrawable() != null && plusImage.get(0).getDrawable() != null && plusImage.get(0).getDrawable() != null && plusImage.get(0).getDrawable() != null && plusImage.get(0).getDrawable() != null) {
                    Toast.makeText(WriteNormalActivity.this, "사진은다섯장만^^", Toast.LENGTH_SHORT).show();
                } else {
                    YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteNormalActivity.this);
                    intent.setMaxSelectCount(5);
                    intent.setShowCamera(true);
                    intent.setShowGif(true);
                    intent.setSelectCheckBox(false);
                    intent.setMaxGrideItemCount(3);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                selectedPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
//            }
//            if (photos != null) {
                for (int i = 0; i < selectedPhotos.size(); i++) {
                    if (plusImage.get(i).getDrawable() == null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(selectedPhotos.get(i)));
                            plusImage.get(i).setImageBitmap(bitmap);
                            Log.d("bitman", String.valueOf(bitmap));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
    private String getTime() {   // 맨 위에 현재 날짜 뽑아 오기.
        now = System.currentTimeMillis();
        date = new Date(now);
        return formate.format(date);
    }
}