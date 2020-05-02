package com.ediantong.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ediantong.bean.CropBean;
import com.ediantong.ui.CropActivity;
import com.ediantong.ui.SelectPictureActivity;
import com.ediantong.utils.FileUtils;

import java.io.File;


public class PictureSelectHelper  {

    private final int CODE_GALLERY_REQUEST = 100;
    private final int CODE_CAMERA_REQUEST = 101;
    public static final int CODE_CROP_RESULT_REQUEST = 102;
    private final int CAMERA_PERMISSIONS_REQUEST_CODE = 201;
    private final int STORAGE_PERMISSIONS_REQUEST_CODE = 202;

    private static final String FOLDER_NAME = Environment.getExternalStorageDirectory().getPath();
    private static final String AUTHROITY = "com.ediantong.provider";
    private AppCompatActivity mContext;

    public File getResultFile() {
        return resultFile;
    }

    private File resultFile;
    private static Uri CAMERA_URI;

    public PictureSelectHelper(AppCompatActivity activity) {
        this.mContext = activity;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_CAMERA_REQUEST://拍照完成回调
                cropImage(mContext, wrapperCropBean(CAMERA_URI, 800, 800, true), CODE_CROP_RESULT_REQUEST);
                break;
            case CODE_GALLERY_REQUEST://访问相册完成回调
                if (hasSdcard() && data != null) {
                    Uri newUri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        newUri = FileProvider.getUriForFile(mContext, AUTHROITY, new File(newUri.getPath()));
                    }
                    cropImage(mContext, wrapperCropBean(newUri, 800, 800, true), CODE_CROP_RESULT_REQUEST);
                } else {
                }
                break;
            case CODE_CROP_RESULT_REQUEST:
                if (data != null) {
                    resultFile = FileUtils.uriToFile(data.getData(), mContext);
                }
                break;
        }
    }


    private CropBean wrapperCropBean(Uri originUri, int width, int height, boolean isSaveRectangle) {
        CropBean cropBean = new CropBean();
        cropBean.originUri = originUri;
        cropBean.height = height;
        cropBean.width = width;
        cropBean.isSaveRectangle = isSaveRectangle;
        cropBean.folder_name = FOLDER_NAME;
        return cropBean;
    }


    /**
     * 申请获取相机权限并且去拍照
     */
    public void cameraPicture() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.CAMERA)) {
            }
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                takePicture(mContext, CODE_CAMERA_REQUEST);
            } else {
            }
        }
    }


    /**
     * 申请相册权限，去打开相册
     */

    public void galleryPicture() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            openPic(mContext, CODE_GALLERY_REQUEST);
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST_CODE: {//调用系统相机申请拍照权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && hasSdcard()) {
                    takePicture(mContext, CODE_CAMERA_REQUEST);
                } else {
                }
                break;
            }
            case STORAGE_PERMISSIONS_REQUEST_CODE://调用系统相册申请Sdcard权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openPic(mContext, CODE_GALLERY_REQUEST);
                } else {
                }
                break;
        }
    }

    /**
     * @param activity    当前activity
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        File cameraFile = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() / 1000 + ".jpg");
        Uri cameraUri = Uri.fromFile(cameraFile);
        CAMERA_URI = cameraUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(activity, AUTHROITY, cameraFile);//通过FileProvider创建一个content类型的Uri
        }
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }

    /**
     * 打开相册选取页  SelectPictureActivity
     *
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SelectPictureActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 接下来进行裁剪操作打开CropActivity
     *
     * @param activity
     * @param cropBean
     * @param requestCode
     */
    public static void cropImage(Activity activity, CropBean cropBean, int requestCode) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra("crop_bean", cropBean);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
