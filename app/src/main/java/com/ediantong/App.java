package com.ediantong;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.ediantong.server.E;
import com.ediantong.utils.SPUtils;

/**
 * Descripition :
 * Created by Liangchaojie on 2019-10-16.
 * Addtion:
 * Update :
 */
public class App extends Application {

    private static App mApp;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mApp, (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initToken(this);
    }

    public static App getInstance() {
        return mApp;
    }

    /**
     * 初始化文字识别sdk token
     *
     * @param context
     */
    private void initToken(Context context) {
        OCR.getInstance(context).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                SPUtils.putString(E.OCR_TOKEN, token);
            }

            @Override
            public void onError(OCRError error) {
                //好坑爹居然是同步任务。
                Message message = Message.obtain();
                message.obj = error.getMessage();
                handler.sendMessage(message);
            }
        }, getApplicationContext(), E.API_KEY, E.SECRET_KEY );
    }
}
