package com.ediantong.library;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.MediaStore;

import com.ediantong.bean.ImageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AlbumImageRunnable implements Runnable {
    private Context mContext;
    private AlbumImageUtils.OnAlbumListener mOnListener;
    private List<ImageBean> mImageList = new ArrayList<>();
    private InnerHandler mHandler = new InnerHandler();
    private static final int SAVE_SUCCESS = 0;

    private class InnerHandler extends Handler {
        public InnerHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case SAVE_SUCCESS:
                    if (mOnListener != null && mImageList.size()>0) {
                        mOnListener.returnImageListOnBackThread(mImageList,Thread.currentThread());
                    }
                    break;
            }
        }
    }

    public AlbumImageRunnable(Context mContext, AlbumImageUtils.OnAlbumListener onListener) {
        this.mContext = mContext;
        this.mOnListener = onListener;
    }

    String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        mImageList.clear();
        Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);
        if (cursor == null) {
            return;
        }
        HashSet<String> albumSet = new HashSet<>();
        File file;

        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }

                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                String image = cursor.getString(cursor.getColumnIndex(projection[1]));
                file = new File(image);
                if (file.exists() && !albumSet.contains(album)) {
                    albumSet.add(album);
                    loadAlbumImages(album);
                }

            } while (cursor.moveToPrevious());
        }
        cursor.close();
        Message.obtain(mHandler, SAVE_SUCCESS, null).sendToTarget();
        Thread.interrupted();
    }


    private void loadAlbumImages(String album) {
        Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{album}, MediaStore.Images.Media.DATE_ADDED);

        if (cursor == null) {
            return;
        }
        HashSet<String> imageSet = new HashSet<>();
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String albumName = cursor.getString(cursor.getColumnIndex(projection[0]));
                String path = cursor.getString(cursor.getColumnIndex(projection[1]));
                long id = cursor.getLong(cursor.getColumnIndex(projection[2]));

                File file = new File(path);
                if (file.exists() && !imageSet.contains(path)) {
                    mImageList.add(new ImageBean(id, albumName, path, false));
                    imageSet.add(path);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }
}
