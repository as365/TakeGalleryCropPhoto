package com.ediantong.library;

import android.content.Context;

import com.ediantong.bean.ImageBean;

import java.util.List;

/**
 * 获取相册图片
 */
public class AlbumImageUtils {

    public interface OnAlbumListener {
        void returnImageListOnBackThread(List<ImageBean> imagePathList,Thread workThread);
    }

    public static void  getImageList(Context context, OnAlbumListener listener){
        AlbumImageRunnable runnable = new AlbumImageRunnable(context,listener);
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
