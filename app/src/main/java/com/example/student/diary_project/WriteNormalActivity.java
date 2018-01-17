package com.example.student.diary_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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
    private ImageView plusImage[] = new ImageView[5];
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
            plusImage[i] = findViewById(tmpID);
        }

        plusImage[4].setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusImage[4].setImageBitmap(null);
            }
        });


        selectImage.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                YPhotoPickerIntent intent = new YPhotoPickerIntent(WriteNormalActivity.this);
                intent.setMaxSelectCount(5);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                intent.setSelectCheckBox(false);
                intent.setMaxGrideItemCount(3);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                Log.i("asd", photos.get(0));
            }
            if (photos != null) {
                selectedPhotos.addAll(photos);
                for (int i = 0; i < selectedPhotos.size(); i++) {
                    Log.i("asd","asd");
                    if(plusImage[i].getDrawable()==null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(selectedPhotos.get(i)));
                            plusImage[i].setImageBitmap(bitmap);
                            Log.i("asdasd", String.valueOf(bitmap));
                        } catch (IOException e) {
                            Log.i("zxc", String.valueOf(e));
                            e.printStackTrace();
                        }
                    }

                }

            }
        }
    }
    private String getTime() {   // 맨 위에 현재 날짜 뽑아 오기.
        now = System.currentTimeMillis();
        date = new Date(now);
        return formate.format(date);
    }


}







