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
        for (int i = 0; i < 6; i++) {
            tmpID = getResources().getIdentifier("iv_write_normal_plusImage" + i, "id", pkg);
            plusImage.add(i, (ImageView) findViewById(tmpID));
        }

        for (int i = 0; i < plusImage.size(); i++) {
            final int finalI = i;
            plusImage.get(i).setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> addPhotos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
                Log.d("qwe", String.valueOf(getIndex())+"//"+addPhotos.size()+"//"+selectedPhotos.size());
                for (int i = getIndex(); i < addPhotos.size()+selectedPhotos.size(); i++) {
                    if (plusImage.get(i).getDrawable() == null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), getUriFromPath(addPhotos.get(i)));
                            plusImage.get(i).setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                selectedPhotos = addPhotos;
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
    private int getIndex(){ // 인덱스값 뽑아오는 메소드
        int result = 0;
        for (int i = 0 ; i <6 ;i++){
            if(plusImage.get(i).getDrawable() == null){
                result = i;
                break;
            }
        }
        return  result;
    }
}