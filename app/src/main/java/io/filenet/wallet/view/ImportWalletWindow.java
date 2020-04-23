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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.filenet.wallet.R;


public class ImportWalletWindow extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {
    private TextView btnCancel;
    private TextView btnPrivateKeyImport;
    private TextView btnMnemonicImport;
    private TextView btnKeyStoreImport;

    private Context mContext;

    private OnInputDialogButtonClickListener onInputDialogButtonClickListener;

    public void setOnInputDialogButtonClickListener(OnInputDialogButtonClickListener onInputDialogButtonClickListener) {
        this.onInputDialogButtonClickListener = onInputDialogButtonClickListener;
    }


    public ImportWalletWindow(@NonNull Context context) {
        super(context);
        mContext = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_load_wallet, null);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth() * 3 / 4);
        setContentView(mView);
        setTouchable(true);
        setFocusable(true);
        setOutsideTouchable(false);
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
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        initView(mView);
        initEvent();
    }

    private void initView(View view) {
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnMnemonicImport = view.findViewById(R.id.mnemonic_import);
        btnPrivateKeyImport = view.findViewById(R.id.private_key_import);
        btnKeyStoreImport = view.findViewById(R.id.key_store_import);
    }


    private void initEvent() {
        btnCancel.setOnClickListener(this);
        btnMnemonicImport.setOnClickListener(this);
        btnPrivateKeyImport.setOnClickListener(this);
        btnKeyStoreImport.setOnClickListener(this);
        setOnDismissListener(this);
    }


    @Override
    public void onClick(View v) {
        if (onInputDialogButtonClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    onInputDialogButtonClickListener.onCancel();
                    dismiss();
                    break;
                case R.id.mnemonic_import:
                    onInputDialogButtonClickListener.onMnemonic();
                    break;
                case R.id.private_key_import:
                    onInputDialogButtonClickListener.onPrivatekey();
                    break;
                case R.id.key_store_import:
                    onInputDialogButtonClickListener.onKeyStore();
                    break;

            }
        }
    }

    @Override
    public void onDismiss() {
        setBackgroundAlpha(1f);
    }


    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams layoutParams = ((Activity) mContext).getWindow().getAttributes();
        layoutParams.alpha = alpha;
        ((Activity) mContext).getWindow().setAttributes(layoutParams);
    }

    public void show() {
        setBackgroundAlpha(0.5f);
        showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    public interface OnInputDialogButtonClickListener {
        void onCancel();

        void onMnemonic();

        void onPrivatekey();

        void onKeyStore();


    }
}
