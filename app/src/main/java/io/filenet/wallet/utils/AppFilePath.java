package io.filenet.wallet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class AppFilePath {

    public static String DATA_ROOT_DIR;
    public static String DATA_ROOT_DIR_OUTER;
    public static String CACHE_ROOT_DIR;
    public static String DB_ROOT_DIR;
    public static String Wallet_DIR;
    public static String Wallet_Tmp_DIR;
    public static String picture;


    @SuppressLint("NewApi")
    public static void init(Context context) {
        final boolean innerFirst = false;
        if (innerFirst) {
            CACHE_ROOT_DIR = context.getCacheDir().getPath();
            DATA_ROOT_DIR = context.getFilesDir().getPath();
            DB_ROOT_DIR = context.getFilesDir().getParent();
            String outerPath = getExternalFilesDir(context).getPath();
            DATA_ROOT_DIR_OUTER = outerPath;
        } else {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                CACHE_ROOT_DIR = getExternalCacheDir(context).getPath();
                DATA_ROOT_DIR = getExternalFilesDir(context).getPath();
                DATA_ROOT_DIR_OUTER = DATA_ROOT_DIR;
                DB_ROOT_DIR = DATA_ROOT_DIR;
            } else {
                CACHE_ROOT_DIR = context.getCacheDir().getPath();
                DATA_ROOT_DIR = context.getFilesDir().getPath();
                DB_ROOT_DIR = context.getFilesDir().getParent();
                DATA_ROOT_DIR_OUTER = DATA_ROOT_DIR;
            }
        }
        Wallet_DIR = getExternalPrivatePath("ethtoken");
        Wallet_Tmp_DIR = getExternalPrivatePath("wallettmp");
        picture = getExternalPrivatePath("picture");
    }

    public static File getExternalFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalFilesDir(null);

            if (path != null) {
                return path;
            }
        }
        final String filesDir = "/Android/data/" + context.getPackageName() + "/files/";
        return new File(Environment.getExternalStorageDirectory().getPath() + filesDir);
    }

    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File path = context.getExternalCacheDir();

            if (path != null) {
                return path;
            }
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @SuppressLint("NewApi")
    public static String getExternalPrivatePath(String name) {
        String namedir = "/" + name + "/";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {

            return Environment.getExternalStorageDirectory().getPath() + namedir;
        } else {
            return new File(DATA_ROOT_DIR_OUTER, name).getPath();
        }
    }
}
