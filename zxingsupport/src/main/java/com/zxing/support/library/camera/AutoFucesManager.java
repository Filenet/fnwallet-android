package com.zxing.support.library.camera;

import android.hardware.Camera;
import android.os.Handler;

public class AutoFucesManager implements Camera.AutoFocusCallback{


    private static final long AUTO_FOCUS_TIMES = 1500L;
    private Camera mCamera;
    private AutoFucesListener mAutoFucesListener;
    private Handler mHandler = new Handler();
    private boolean isAutoFucesIng = false;

    public void start(AutoFucesListener autoFucesListener) {
        mAutoFucesListener = autoFucesListener;
        if (mCamera != null && !isAutoFucesIng){
            mCamera.autoFocus(this);
            isAutoFucesIng = true;
        }
    }

    public void stop() {
        isAutoFucesIng = false;
        mCamera = null;
        mHandler.removeCallbacks(mAutoRunnable);
    }

    public void setCamera(Camera camera) {
        this.mCamera = camera;
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        if (mAutoFucesListener != null){
            mAutoFucesListener.onAutoFocus(success, camera);
        }
        if (isAutoFucesIng){
            mHandler.postDelayed(mAutoRunnable,AUTO_FOCUS_TIMES);
        }
    }

    private Runnable mAutoRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCamera != null){
                mCamera.autoFocus(AutoFucesManager.this);
            }
        }
    };
}
