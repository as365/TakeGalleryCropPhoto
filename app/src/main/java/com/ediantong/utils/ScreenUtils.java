package com.ediantong.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {

    public static int getScreenWidth(Context context){
        int result = 0;
        if(context instanceof Activity){
            WindowManager manager = ((Activity)context).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            result = outMetrics.widthPixels;
        }
        return result;
    }

    public static int getScreenHeight(Context context){
        int result = 0;
        if(context instanceof Activity){
            WindowManager manager = ((Activity)context).getWindowManager();
            DisplayMetrics outMetrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(outMetrics);
            result = outMetrics.heightPixels;
        }
        return result;
    }
}
