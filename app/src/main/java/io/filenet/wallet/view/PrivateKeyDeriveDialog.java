package io.filenet.wallet.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.TextView;

import io.filenet.wallet.R;


public class PrivateKeyDeriveDialog extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {


    private TextView tvPrivateKey;
    private TextView btnCopy;

    public void setPrivateKey(String privateKey) {
        tvPrivateKey.setText(privateKey);
    }
    private Context mContext;


    public PrivateKeyDeriveDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_derive_private_key, null);
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

    private void initEvent(View view) {
        btnCopy = view.findViewById(R.id.btn_confirm);
        view.findViewById(R.id.btn_cancel).setOnClickListener(this);
        tvPrivateKey = view.findViewById(R.id.tv_private_key);
        btnCopy.setOnClickListener(this);
        view.findViewById(R.id.lly_close).setOnClickListener(this);
        setOnDismissListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", tvPrivateKey.getText().toString());
                    cm.setPrimaryClip(mClipData);
                }
                btnCopy.setText(R.string.derive_private_key_already_copy_btn);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
        }
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

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1f);
    }
}
