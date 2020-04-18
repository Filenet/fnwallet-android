package com.zxing.support.library.view;

import com.google.zxing.ResultPointCallback;
import com.zxing.support.library.camera.CameraManager;

public interface QRCodeFindView extends ResultPointCallback {

    void setCamanerManager(CameraManager manager);
}
