package com.ediantong.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.ediantong.utils.ToastUtil;

import java.io.File;
import java.io.Serializable;

/**
 * by liangchaojie
 * <p>
 * 我们平时写拍照上传,相册选取的时候要拷贝一大串代码，复制粘贴特别恶心，所以写了一个工具类帮助生成模板代码
 * <p>
 * 具体使用请看
 */
public class PictureSelectHelper implements Serializable {

    private final int CODE_GALLERY_REQUEST = 100;
    private final int CODE_CAMERA_REQUEST = 101;
    private final int CODE_CROP_RESULT_REQUEST = 102;
    private final int CAMERA_PERMISSIONS_REQUEST_CODE = 201;
    private final int STORAGE_PERMISSIONS_REQUEST_CODE = 202;
    public final File FILE_URI = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    public final File FILE_CROP_URI = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private static final String FOLDER_NAME = Environment.getExternalStorageDirectory().getPath();

    private final String AUTHROITY = "com.ediantong.provider";
    private Uri imageUri;
    private Uri cropImageUri;
    private int output_X = 800;
    private int output_Y = 800;

    private AppCompatActivity mContext;
    private Bitmap resultBitmap;

    public PictureSelectHelper(AppCompatActivity activity) {
        this.mContext = activity;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CODE_CAMERA_REQUEST://拍照完成回调
                cropImageUri = Uri.fromFile(FILE_CROP_URI);
                cropImage(mContext, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_CROP_RESULT_REQUEST);
                break;
            case CODE_GALLERY_REQUEST://访问相册完成回调
                if (hasSdcard()) {
                    cropImageUri = Uri.fromFile(FILE_CROP_URI);
                    if (data != null) {
                        Uri newUri = data.getData();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            newUri = FileProvider.getUriForFile(mContext, AUTHROITY, new File(newUri.getPath()));
                        }
                        cropImage(mContext, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_CROP_RESULT_REQUEST);
                    }
                } else {
                    ToastUtil.showCenterShort("设备没有SD卡！");
                }
                break;
            case CODE_CROP_RESULT_REQUEST:
                resultBitmap = getBitmapFromUri(cropImageUri, mContext);
                break;
        }
    }


    /**
     * 申请获取相机权限并且去拍照
     */
    public void cameraPicture() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, Manifest.permission.CAMERA)) {
                ToastUtil.showCenterShort("您已经拒绝过一次!");
            }
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(FILE_URI);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    imageUri = FileProvider.getUriForFile(mContext, AUTHROITY, FILE_URI);//通过FileProvider创建一个content类型的Uri
                takePicture(mContext, imageUri, CODE_CAMERA_REQUEST);
            } else {
                ToastUtil.showCenterShort("设备没有SD卡！");
            }
        }
    }


    /**
     * 申请相册权限，去打开相册
     */

    public void galleryPicture() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSIONS_REQUEST_CODE);
        } else {
            openPic(mContext, CODE_GALLERY_REQUEST);
        }

    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSIONS_REQUEST_CODE: {//调用系统相机申请拍照权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (hasSdcard()) {
                        imageUri = Uri.fromFile(FILE_URI);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            imageUri = FileProvider.getUriForFile(mContext, AUTHROITY, FILE_URI);//通过FileProvider创建一个content类型的Uri
                        takePicture(mContext, imageUri, CODE_CAMERA_REQUEST);
                    } else {
                        ToastUtil.showCenterShort("设备没有SD卡！");
                    }
                } else {
                    ToastUtil.showCenterShort("请允许打开相机！！");
                }
                break;
            }
            case STORAGE_PERMISSIONS_REQUEST_CODE://调用系统相册申请Sdcard权限回调
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPic(mContext, CODE_GALLERY_REQUEST);
                } else {
                    ToastUtil.showCenterShort("请允许打操作SDCard！！");
                }
                break;
        }
    }

    public Bitmap getResultBitmap() {
        return resultBitmap;
    }

    /***********************************下面都是辅助方法****************************************/

    private static final String TAG = "PhotoUtils";

    /**
     * @param activity    当前activity
     * @param imageUri    拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }

    /**
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity activity, int requestCode) {
//        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//        photoPickerIntent.setType("image/*");
//        activity.startActivityForResult(photoPickerIntent, requestCode);

        Intent intent = new Intent(activity, SelectPictureActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param activity    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImage(AppCompatActivity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {

//        Intent intent = new Intent("com.android.camera.action.CROP");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        intent.setDataAndType(orgUri, "image/*");
//        intent.putExtra("crop", "true");
//        if (width * height > 0) {
//            //矩形裁剪
//            intent.putExtra("outputX", width);
//            intent.putExtra("outputY", height);
//        } else {
//            //圆形裁剪
//            intent.putExtra("aspectX", aspectX);
//            intent.putExtra("aspectY", aspectY);
//        }
//
//        intent.putExtra("scale", true);
//        //将剪切的图片保存到目标Uri中
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
//        //如果return-data  返回true的话  容易产生android.os.TransactionTooLargeException异常
//        //这里不要其返回数据而是让开发者从uri去取文件
//        intent.putExtra("return-data", isReturnData());
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true);
//        activity.startActivityForResult(intent, requestCode);
        CropBean cropBean = new CropBean();
        cropBean.desUri = desUri;
        cropBean.originUri = orgUri;
        cropBean.height = height;
        cropBean.width = width;
        cropBean.folder_name = FOLDER_NAME;
        Intent intent = new Intent(activity, CropActivity.class);
        intent.putExtra("crop_bean",cropBean);
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * 读取uri所在的图片
     *
     * @param uri      图片对应的Uri
     * @param mContext 上下文对象
     * @return 获取图像的Bitmap
     */
    public static Bitmap getBitmapFromUri(Uri uri, Context mContext) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}
