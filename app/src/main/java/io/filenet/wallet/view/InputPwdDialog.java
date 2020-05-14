package io.filenet.wallet.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.filenet.wallet.R;
import io.filenet.wallet.utils.TKeybord;
import io.filenet.wallet.utils.ToastUtils;


public class InputPwdDialog extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {
    private EditText etPwd;
    private TextView btnCancel;
    private TextView btnConfirm;
    private TextView tvDialogTitle;
    private TextView tvDialogTips;

    private TextView tvDeleteAlert;
    private Context mContext;

    private OnInputDialogButtonClickListener onInputDialogButtonClickListener;

    public void setOnInputDialogButtonClickListener(OnInputDialogButtonClickListener onInputDialogButtonClickListener) {
        this.onInputDialogButtonClickListener = onInputDialogButtonClickListener;
    }


    public InputPwdDialog(@NonNull Context context) {
        super(context);
        mContext = context;
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_input_pwd, null);
        setContentView(mView);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setWidth(((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth() * 3 / 4);

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
        initView(mView);
        initEvent();
    }

    private void initView(View view) {
        etPwd = view.findViewById(R.id.et_pwd);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnConfirm = view.findViewById(R.id.btn_confirm);
        tvDeleteAlert = view.findViewById(R.id.tv_delete_alert);
        tvDialogTitle = view.findViewById(R.id.dialog_password_title);
        tvDialogTips = view.findViewById(R.id.dialog_password_tips);
    }

    public void setDialogTitle(String title) {
        tvDialogTitle.setText(title);
    }


    private void initEvent() {
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        setOnDismissListener(this);
    }

    public void setDeleteAlertVisibility(boolean visibility) {
        if (tvDeleteAlert != null)
            if (visibility) {
                tvDeleteAlert.setVisibility(View.VISIBLE);
            } else {
                tvDeleteAlert.setVisibility(View.GONE);
            }
    }


    @Override
    public void onClick(View v) {
        if (onInputDialogButtonClickListener != null) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    onInputDialogButtonClickListener.onCancel();
                    dismiss();
                    break;
                case R.id.btn_confirm:
                    String pwd = etPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(pwd)) {
                        ToastUtils.showToast(mContext, R.string.input_pwd_dialog_tip);
                        return;
                    }
                    onInputDialogButtonClickListener.onConfirm(pwd);
                    break;
            }
        }
    }

    @Override
    public void onDismiss() {
        etPwd.setText("");
        etPwd.clearFocus();
        setBackgroundAlpha(1f);
        TKeybord.closeKeybord(etPwd);
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

    public interface OnInputDialogButtonClickListener {
        void onCancel();

        void onConfirm(String pwd);
    }
}
