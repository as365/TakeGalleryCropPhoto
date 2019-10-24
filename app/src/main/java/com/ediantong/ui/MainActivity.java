package com.ediantong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ediantong.R;
import com.ediantong.helpers.PictureSelectHelper;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btmCamera;
    private Button btmPic;
    private TextView tv_ocr;
    private PictureSelectHelper helper = new PictureSelectHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btmCamera = findViewById(R.id.btm_camera);
        tv_ocr = findViewById(R.id.tv_ocr);
        btmPic = findViewById(R.id.btm_pic);
        btmCamera.setOnClickListener(this);
        btmPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btm_camera:
                helper.cameraPicture();
                break;
            case R.id.btm_pic:
                helper.galleryPicture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
        //只有成功获取到了bitmap才进行上传处理
        if (helper.getResultFile() != null) {
            upLoadBitmap(helper.getResultFile());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void upLoadBitmap(File file) {
        tv_ocr.setText("");

    }

}
