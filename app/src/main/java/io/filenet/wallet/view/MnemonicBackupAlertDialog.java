package io.filenet.wallet.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import io.filenet.wallet.R;


public class MnemonicBackupAlertDialog extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {


    private final Context mContext;

    public MnemonicBackupAlertDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_mnemonic_backup_alert, null);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setWidth(((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth() * 3 / 4);
        setContentView(mView);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(false);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isOutsideTouchable()) {
                    View mView = getContentView();
                    if (null != mView)
                        mView.dispatchTouchEvent(event);
                }
                return isFocusable() && !isOutsideTouchable();
            }
        });
        initEvent(mView);
    }
    private void initEvent(View mView) {
        mView.findViewById(R.id.btn_confirm).setOnClickListener(this);
        setOnDismissListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
        }
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1f);
    }


    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams layoutParams = ((Activity) mContext).getWindow().getAttributes();
        layoutParams.alpha = alpha;
        ((Activity) mContext).getWindow().setAttributes(layoutParams);
    }

    public void show() {
        setBackgroundAlpha(0.5f);
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }


}
