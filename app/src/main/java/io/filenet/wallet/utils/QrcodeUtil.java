package io.filenet.wallet.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import io.filenet.wallet.R;

public class QrcodeUtil {

    public static boolean copyQrcode(Context mContext, CharSequence labelText){
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            ClipData mClipData = ClipData.newPlainText("Label", labelText);
            cm.setPrimaryClip(mClipData);
        }
        ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
        return true;
    }
}
