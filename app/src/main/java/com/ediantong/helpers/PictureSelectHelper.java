package com.ediantong.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;

import com.ediantong.R;
import com.ediantong.utils.ToastUtil;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;


import static android.app.Activity.RESULT_OK;

/**
 * by liangchaojie
 * <p>
 * 我们平时写拍照上传,相册选取的时候要拷贝一大串代码，复制粘贴特别恶心，所以写了一个工具类帮助生成模板代码
 * <p>
 * 具体使用请看
 */
public class PictureSelectHelper implements Serializable {

    private  final int CODE_GALLERY_REQUEST = 0xa0;
    private  final int CODE_CAMERA_REQUEST = 0xa1;
    private  final int CODE_RESULT_REQUEST = 0xa2;
    private  final int CAMERA_PERMISSIONS_REQUEST_CODE = 0x03;
    private  final int STORAGE_PERMISSIONS_REQUEST_CODE = 0x04;
    public  final File FILE_URI = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    public  final File FILE_CROP_URI = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private final String AUTHROITY = "com.ediantong.provider";
    private Uri imageUri;
    private Uri cropImageUri;
    private int output_X = 480;
    private int output_Y = 480;

    private Activity mContext;
    private Bitmap resultBitmap;

    /**
     * 编辑个人资料头像显示选择图片对话框
     *
     * @param activity
     */
    public void showSelectPicturePopForEditProfile(FragmentActivity activity, int maxSelectNum) {
        if (activity == null) {
            return;
        }
        this.mContext = activity;
    }

    public void  init(FragmentActivity activity){
        this.mContext = activity;
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 申请获取相机权限并且去拍照
     */
    public void autoObtainCameraPermission() {

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

    public void autoObtainGalleryPermission() {
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


    public void onActivityResult(int requestCode, int resultCode, Intent data,OnBitmapListener onBitmapListener) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CODE_CAMERA_REQUEST://拍照完成回调
                cropImageUri = Uri.fromFile(FILE_CROP_URI);
                cropImageUri(mContext, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                break;
            case CODE_GALLERY_REQUEST://访问相册完成回调
                if (hasSdcard()) {
                    cropImageUri = Uri.fromFile(FILE_CROP_URI);
                    Uri newUri = Uri.parse(getPath(mContext, data.getData()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        newUri = FileProvider.getUriForFile(mContext, AUTHROITY, new File(newUri.getPath()));
                    cropImageUri(mContext, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                } else {
                    ToastUtil.showCenterShort("设备没有SD卡！");
                }
                break;
            case UCrop.REQUEST_CROP:
                Bitmap bitmap = getBitmapFromUri(cropImageUri, mContext);
                if (bitmap != null && onBitmapListener!=null) {
                    resultBitmap = bitmap;
                    onBitmapListener.successBitmap(resultBitmap);
                }
                break;
        }
    }


    public Bitmap getResultBitmap(){
        return resultBitmap;
    }

    public interface OnBitmapListener{
        void successBitmap(Bitmap bitmap);
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
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
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
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {

        UCrop.of(orgUri, desUri)
                .withAspectRatio(16, 9)
                .withMaxResultSize(width, height)
                .start(activity);

//        Intent intent = new Intent("com.android.camera.action.CROP");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        intent.setDataAndType(orgUri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", width);
//        intent.putExtra("outputY", height);
//        intent.putExtra("scale", true);
//        //将剪切的图片保存到目标Uri中
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
//        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true);
//        activity.startActivityForResult(intent, requestCode);
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
     * @param context 上下文对象
     * @param uri     当前相册照片的Uri
     * @return 解析后的Uri对应的String
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String pathHead = "file:///";
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return pathHead + Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return pathHead + getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return pathHead + getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return pathHead + uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
