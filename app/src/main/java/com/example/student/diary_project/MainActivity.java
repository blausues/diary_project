package com.example.student.diary_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnThema, btnWrite, btnSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_day);

        btnThema = findViewById(R.id.btn_thema);
        btnWrite = findViewById(R.id.btn_write);
        btnSetting = findViewById(R.id.btn_setting);

        btnThema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"dsad",Toast.LENGTH_SHORT).show();
            }
        });
=======
        setContentView(R.layout.activity_write_diet);
>>>>>>> 4a8fb1bca2f3171ca38ac5a647c40ae9c0f0706b
    }
}
