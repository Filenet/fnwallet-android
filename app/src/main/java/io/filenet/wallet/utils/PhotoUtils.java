package io.filenet.wallet.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

public class PhotoUtils {

    public static final int REQUEST_CODE_PHOTO = 1;
    public static final int REQUEST_CODE_ALDUM = 2;
    public static final int REQUEST_CODE_CROP = 3;

    public static final File picFile;
    public static final File userIconFile;

    static {
        File baseFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "dapp/image");
        if (!baseFile.exists()) {
            baseFile.mkdirs();
        }
        picFile = new File(baseFile, System.currentTimeMillis() + "temp.jpg");
        userIconFile = new File(baseFile, System.currentTimeMillis() + "crop.jpg");
    }


    public static void takePhoto(Activity mActivity) {

        Intent intent = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri imgUri = FileProvider.getUriForFile(mActivity, "io.filenet.wallet.fileProvider", picFile);
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                mActivity.startActivityForResult(intent, REQUEST_CODE_PHOTO);
            } else {
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picFile));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true);
                mActivity.startActivityForResult(intent, REQUEST_CODE_PHOTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void aldumPhoto(Activity mActivity) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mActivity.startActivityForResult(intent, REQUEST_CODE_ALDUM);
    }

    public static void startPhotoZoom(Activity mActivity, Uri uri) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 350);
                intent.putExtra("outputY", 350);
                intent.putExtra("scale", true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userIconFile));
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true);
                mActivity.startActivityForResult(intent, REQUEST_CODE_CROP);
            } else {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.putExtra("crop", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 350);
                intent.putExtra("outputY", 350);
                intent.putExtra("return-data", false);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(userIconFile));
                intent.putExtra("noFaceDetection", true);
                mActivity.startActivityForResult(intent, REQUEST_CODE_CROP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}