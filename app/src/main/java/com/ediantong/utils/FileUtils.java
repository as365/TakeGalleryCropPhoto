package com.ediantong.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Descripition :
 * Created by Liangchaojie on 2019-10-16.
 * Addtion:
 * Update :
 */
public class FileUtils {

    private static final String RESULT_FILE_NAME = Environment.getExternalStorageDirectory().getPath() + "/temp.jpg";
    /**
     * 根据bitmap获取文件
     * @param bitmap
     * @return
     */
    public static File getFile(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        File file = new File(RESULT_FILE_NAME);
        try {
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(baos.toByteArray());
            int x = 0;
            byte[] b = new byte[1024 * 100];
            while ((x = is.read(b)) != -1) {
                fos.write(b, 0, x);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
