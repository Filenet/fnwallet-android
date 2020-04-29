package io.filenet.wallet.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zxing.support.library.QRCodeSupport;
import com.zxing.support.library.qrcode.QRCodeDecode;
import com.zxing.support.library.view.FinderViewStyle2;

import butterknife.BindView;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.utils.ToastUtils;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static io.filenet.wallet.utils.PhotoUtils.REQUEST_CODE_ALDUM;
import static io.filenet.wallet.utils.PhotoUtils.aldumPhoto;


public class QRCodeScannerActivity extends BaseActivity implements QRCodeSupport.OnScanResultListener, View.OnClickListener {
    private static final String TAG = QRCodeScannerActivity.class.getSimpleName();
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 123;
    private static final int QRCODE_RESULT = 124;

    private static final int SCAN_W = 720;
    private static final int SCAN_H = 720;
    @BindView(R.id.scanner_toolsbar)
    Toolbar scannerToolbar;

    private RelativeLayout rlGallery;
    private RelativeLayout rlFlashLight;
    private LinearLayout llBack;
    private String path;
    private SurfaceView mSurfaceView;
    private QRCodeSupport mQRCodeSupport;
    private FinderViewStyle2 mFinderView;

    private Disposable disposable;

    private boolean isStop = false;


    @Override
    public int getLayoutId() {
        return R.layout.activity_qrcode_scanner;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .titleBar(scannerToolbar, false)
                .navigationBarColor(R.color.colorPrimary)
                .init();
        initScanner();
    }

    private void initScanner() {
        rlGallery = findViewById(R.id.rl_gallery);
        rlFlashLight = (RelativeLayout) findViewById(R.id.rl_flash_light);
        llBack = (LinearLayout) findViewById(R.id.lly_back);
        rlGallery.setOnClickListener(this);
        rlFlashLight.setOnClickListener(this);
        llBack.setOnClickListener(this);

        mFinderView = (FinderViewStyle2) findViewById(R.id.capture_viewfinder_view);
        mSurfaceView = (SurfaceView) findViewById(R.id.sufaceview);
        mQRCodeSupport = new QRCodeSupport(mSurfaceView, mFinderView);
        mQRCodeSupport.setScanResultListener(this);

        ImmersionBar.with(this)
                .titleBar(scannerToolbar, false)
                .statusBarColor(R.color.colorPrimary)
                .navigationBarColor(R.color.colorPrimary)
                .init();
    }

    @Override
    public void onScanResult(String notNullResult, byte[] resultByte) {
        Intent data = new Intent();
        data.putExtra("scan_result", notNullResult);
        setResult(QRCODE_RESULT, data);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.rl_flash_light:
                new RxPermissions(this)
                        .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    aldumPhoto(QRCodeScannerActivity.this);
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    new AlertDialog.Builder(QRCodeScannerActivity.this)
                                            .setMessage(R.string.allow_storage_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                } else {
                                    new AlertDialog.Builder(QRCodeScannerActivity.this)
                                            .setTitle(R.string.string_apply)
                                            .setMessage(R.string.allow_storage_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                }
                            }
                        });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isStop) {
            initScanner();
            isStop = false;
        }
        mQRCodeSupport.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQRCodeSupport.onPause();

        isStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ALDUM) {
            disposable = Single.create((SingleOnSubscribe<String>) emitter -> {
                if (null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    String imagePath;
                    Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    if (c != null) {
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        imagePath = c.getString(columnIndex);
                        c.close();
                        Bitmap bm = BitmapFactory.decodeFile(imagePath);
                        String result = new QRCodeDecode.Builder().build().decode(bm);
                        emitter.onSuccess(result);
                    } else {
                        emitter.onError(new Exception(""));
                    }
                }
            }).observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        data.putExtra("scan_result", s);
                        setResult(QRCODE_RESULT, data);
                        finish();
                    }, throwable -> {
                        ToastUtils.showToast(mContext, R.string.picture_path_is_empty);
                    });
        }

    }


}
