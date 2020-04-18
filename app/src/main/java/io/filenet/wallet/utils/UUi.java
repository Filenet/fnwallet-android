package io.filenet.wallet.utils;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;

import io.filenet.wallet.ETHTokenApplication;


public class UUi {

    @Deprecated
    public static  <T extends View> T getView(Activity activity, SparseArray<View> mViews, int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) activity.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    public static  <T extends View> T getView(View mView, SparseArray<View> mViews, int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) mView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }

    @Deprecated
    public static void setOnClickListener(View.OnClickListener listener, View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            view.setOnClickListener(listener);
        }

    }


    public static int dip2px(float dipValue) {
        float scale = ETHTokenApplication.getsInstance().getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        float scale = ETHTokenApplication.getsInstance().getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    public static int px2sp(float pxValue) {
        float fontScale = ETHTokenApplication.getsInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / fontScale + 0.5F);
    }

    public static int sp2px(float spValue) {
        float fontScale = ETHTokenApplication.getsInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5F);
    }

}
