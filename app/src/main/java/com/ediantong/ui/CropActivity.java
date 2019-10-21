package com.ediantong.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ediantong.R;

import java.io.File;


public class CropActivity extends AppCompatActivity {

    private String path = "";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        initView();
        initPath(getIntent());
        doLogic();
    }

    private void initView() {
        imageView = findViewById(R.id.imageView);
    }

    private void initPath(Intent intent) {
        if(intent!=null){
            path = intent.getStringExtra("path");
        }
    }

    private void doLogic() {
        Glide.with(this).load(new File(path)).into(imageView);
    }
}
