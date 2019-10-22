package com.ediantong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ediantong.R;
import com.ediantong.bean.CropBean;
import com.ediantong.utils.ToastUtil;
import com.ediantong.view.CropImageView;

import java.io.File;


public class CropActivity extends AppCompatActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {

    private CropBean mCropBean;
    private CropImageView cropImageView;
    private TextView tv_cancel, tv_ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowFlags();
        setContentView(R.layout.activity_crop);
        setupViews();
    }

    private void setupViews() {
        initView();
        initPath(getIntent());
        doLogic();
    }

    private void setupWindowFlags() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initView() {
        cropImageView = findViewById(R.id.cropImageView);
        tv_cancel = findViewById(R.id.tv_back);
        tv_ok = findViewById(R.id.tv_ok);
        tv_cancel.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        cropImageView.setOnBitmapSaveCompleteListener(this);
    }

    private void initPath(Intent intent) {
        if (intent != null) {
            mCropBean = (CropBean) intent.getParcelableExtra("crop_bean");
        }
    }

    private void doLogic() {
        if (mCropBean != null) {
            Glide.with(this).load(mCropBean.originUri).into(cropImageView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                saveFile();
                break;
            case R.id.tv_back:
                finish();
                break;
        }
    }

    private void saveFile() {
        if(mCropBean!=null){
            File file = new File(mCropBean.folder_name);
            cropImageView.saveBitmapToFile(file, mCropBean.width, mCropBean.height, mCropBean.isSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        finish();
        ToastUtil.showCenterShort("success");
    }

    @Override
    public void onBitmapSaveError(File file) {
        ToastUtil.showCenterShort("fail");
    }
}
