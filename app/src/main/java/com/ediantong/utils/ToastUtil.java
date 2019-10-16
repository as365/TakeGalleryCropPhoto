package com.ediantong.utils;

import android.text.TextUtils;
import android.widget.Toast;

import com.ediantong.App;


/**
 * @Date: 2018/11/13
 * @Author: heweizong
 * @Description:
 */
public class ToastUtil {

    public static void showCenterShort(CharSequence msg) {
        if (TextUtils.isEmpty(msg)) return;
        Toast.makeText(App.getInstance(), msg, Toast.LENGTH_SHORT).show();
    }


}
