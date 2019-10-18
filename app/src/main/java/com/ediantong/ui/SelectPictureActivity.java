package com.ediantong.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ediantong.R;

import java.io.File;
import java.util.HashSet;

public class SelectPictureActivity extends AppCompatActivity {
    String[] projection = new String[]{ MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAlbums();
    }

    private void loadAlbums() {
        AlbumLoaderRunnable runnable = new AlbumLoaderRunnable();
        Thread thread = new Thread(runnable);
        thread.start();
    }


    private class AlbumLoaderRunnable implements Runnable {
        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Cursor cursor = getApplicationContext().getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                            null, null, MediaStore.Images.Media.DATE_ADDED);
            if(cursor==null){
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

                    /*
                    It may happen that some image file paths are still present in cache,
                    though image file does not exist. These last as long as media
                    scanner is not run again. To avoid get such image file paths, check
                    if image file exists.
                     */
                    file = new File(image);
                    if (file.exists() && !albumSet.contains(album)) {
                        albumSet.add(album);
                        Log.i("TAG", "loadAlbum: "+album);
                        loadAlbumImages(album);
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            Thread.interrupted();
        }
    }

    private void loadAlbumImages(String album) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =?", new String[]{ album }, MediaStore.Images.Media.DATE_ADDED);

        if (cursor == null) {
            return;
        }


        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }

                String name = cursor.getString(cursor.getColumnIndex(projection[0]));
                String path = cursor.getString(cursor.getColumnIndex(projection[1]));

                File file = new File(path);
                if (file.exists()) {
                    Log.i("TAG", "loadAlbumImages: "+path);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }
}
