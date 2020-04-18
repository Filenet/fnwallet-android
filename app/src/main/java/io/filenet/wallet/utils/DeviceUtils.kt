package io.filenet.wallet.utils

import android.content.Context

object DeviceUtils {
    fun getVersionName(mContext: Context): String {
        return mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName
    }
}