package com.zxing.support.library;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zxing.support.library.camera.AutoFucesListener;
import com.zxing.support.library.camera.CameraManager;
import com.zxing.support.library.qrcode.QRCodeCameraDecode;
import com.zxing.support.library.view.QRCodeFindView;

import java.io.IOException;

public class QRCodeSupport {


    private static final String TAG = QRCodeSupport.class.getSimpleName();
    private SurfaceView mSurfaceView;
    private QRCodeFindView mQRCodeFindView;
    private OnScanResultListener mOnScanResultListener;
    private CameraManager mCameraManager;
    private QRCodeCameraDecode mCameraDecode;
    private boolean isPrivew;



    public QRCodeSupport(SurfaceView surfaceView, QRCodeFindView findView) {
        this.mSurfaceView = surfaceView;
        this.mQRCodeFindView = findView;
        mCameraManager = new CameraManager(surfaceView.getContext().getApplicationContext());
        mQRCodeFindView.setCamanerManager(mCameraManager);
    }


    public void setScanResultListener(OnScanResultListener onScanResultListener){
        this.mOnScanResultListener = onScanResultListener;
    }

    private AutoFucesListener mAutoFucesListener = new AutoFucesListener() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    private final Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraDecodeTask mCameraDecodeTask = new CameraDecodeTask();
            mCameraDecodeTask.execute(data);
        }
    };

    private SurfaceHolder.Callback mSurfaceViewCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            initCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isPrivew = false;
        }
    };

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (mCameraManager.isOpen()) return;

        try {
            isPrivew = true;
            mCameraManager.openDevice(surfaceHolder);
            mCameraManager.requestPreview(mPreviewCallback);
            mCameraManager.startPreview();
            mCameraManager.setAutoFucesListener(mAutoFucesListener);

        } catch (IOException e) {
            e.printStackTrace();
            isPrivew = false;
        }


    }

    public void onResume() {
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(mSurfaceViewCallback);
    }

    public void onPause() {
        isPrivew = false;
        SurfaceHolder surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.removeCallback(mSurfaceViewCallback);
        mCameraManager.stopPreview();
        mCameraManager.closeDriver();
    }



    private class CameraDecodeTask extends AsyncTask <byte[], Void, QRCodeCameraDecode.CameraDecodeResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCameraDecode == null){
                mCameraDecode = new QRCodeCameraDecode(mCameraManager,mQRCodeFindView);
            }
        }

        @Override
        protected void onPostExecute(QRCodeCameraDecode.CameraDecodeResult result) {
            super.onPostExecute(result);
            if (result.getDecodeResult() == null){
                if (isPrivew)
                mCameraManager.requestPreview(mPreviewCallback);
            }else {

                String resultString = result.getDecodeResult().getText();

                if (!TextUtils.isEmpty(resultString) && mOnScanResultListener != null){

                    mOnScanResultListener.onScanResult(resultString,result.getDecodeByte());
                }
            }
        }

        @Override
        protected QRCodeCameraDecode.CameraDecodeResult doInBackground(byte[]... params) {

            return mCameraDecode.decode(params[0]);
        }
    }

    public boolean toggleFlashLight() {
        return mCameraManager.toggleFlashLight();
    }

    public boolean isOpenFlashLight() {
        return mCameraManager.isOpenFlashLight();
    }


    public interface OnScanResultListener{
        void onScanResult(String notNullResult,byte[] resultByte);
    }

}
