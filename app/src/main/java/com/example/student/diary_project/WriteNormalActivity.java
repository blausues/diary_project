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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private int PICK_IMAGE_REQUEST = 1;
    private int tmpID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_normal);

        tv_date = findViewById(R.id.tv_write_normal_date);
        tv_date.setText(getTime());
        selectImage = findViewById(R.id.ib_select_image);

        String pkg = getPackageName();   // findViewById 반복문 돌리기
        for (int i = 0; i < 5; i++) {
            tmpID = getResources().getIdentifier("iv_write_normal_plusImage"+i, "id", pkg);
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
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

    }

    private String getTime() {   // 맨 위에 현재 날짜 뽑아 오기.
        now = System.currentTimeMillis();
        date = new Date(now);
        return formate.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            for (int i = 0; i < 5; i++) {
                if (plusImage[i].getDrawable() == null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        ExifInterface exif = new ExifInterface(uri.getPath());
                        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        int exifDegree = exifOrientationToDegrees(exifOrientation);
                        bitmap = rotate(bitmap, exifDegree);

                        plusImage[i].setImageBitmap(bitmap);
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(plusImage[4].getDrawable() != null){
                    Toast.makeText(this, "사진은 최대 5장!!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    public int exifOrientationToDegrees(int exifOrientation){
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180){
            return 180;
        }else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270){
            return 270;
        } return 0;

    }
    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }


}
