package com.ediantong.library;

import android.content.Context;
import android.database.Cursor;
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

    public AlbumImageRunnable(Context mContext, AlbumImageUtils.OnAlbumListener onListener) {
        this.mContext = mContext;
        this.mOnListener = onListener;
    }

    String[] projection = new String[]{MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};

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
        mOnListener.returnImageListOnBackThread(mImageList,Thread.currentThread());
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
                    mImageList.add(new ImageBean(id,albumName,path,false));
                    imageSet.add(path);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }
}
