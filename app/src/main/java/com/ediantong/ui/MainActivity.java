package com.ediantong.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;
import com.ediantong.R;
import com.ediantong.helpers.PictureSelectHelper;
import com.ediantong.utils.FileUtils;
import com.ediantong.utils.ToastUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btmCamera;
    private Button btmPic;
    private TextView tv_ocr;
    private PictureSelectHelper helper = new PictureSelectHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        helper.init(this);
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
                helper.autoObtainCameraPermission();
                break;
            case R.id.btm_pic:
                helper.autoObtainGalleryPermission();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
        //只有成功获取到了bitmap才进行上传处理
        if (helper.getResultBitmap() != null) {
            upLoadBitmap(helper.getResultBitmap());
        }
        Log.i("TAG", "onActivityResult: "+requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        helper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    /**
     * 申请ocr识别图片
     * @param bitmap
     */
    private void upLoadBitmap(Bitmap bitmap) {
        // 通用文字识别参数设置
        GeneralBasicParams param = new GeneralBasicParams();
        param.setDetectDirection(true);
        param.setImageFile(FileUtils.getFile(bitmap));

        // 调用通用文字识别服务
        OCR.getInstance(this).recognizeGeneralBasic(param, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                tv_ocr.setText(getUpLoadOcrText(result));
            }

            @Override
            public void onError(OCRError error) {
                ToastUtil.showCenterShort(error.getMessage());
            }
        });

    }

    /**
     * 获取ocr识别结果并且封装
     *
     * @param result
     * @return
     */
    private String getUpLoadOcrText(GeneralResult result) {
        StringBuffer sb = new StringBuffer();
        for (WordSimple wordSimple : result.getWordList()) {
            sb.append(wordSimple.getWords());
        }
        Log.i("TAG", "getUpLoadOcrText: " + sb.toString());
        return sb.toString();
    }
}
