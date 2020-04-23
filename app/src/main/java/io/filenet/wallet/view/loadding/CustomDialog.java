package io.filenet.wallet.view.loadding;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import io.filenet.wallet.R;


public class CustomDialog extends Dialog {

    private static TextView tvProgress;
    private static LoadingView loadingView;

    public CustomDialog(Context context) {
        this(context, 0);
    }

    public CustomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static CustomDialog instance(Activity activity) {
        View v = View.inflate(activity, R.layout.common_progress_view, null);
        loadingView = v.findViewById(R.id.loadingView);
        tvProgress = v.findViewById(R.id.tv_progress);
        CustomDialog dialog = new CustomDialog(activity, R.style.loading_dialog);
        dialog.setContentView(v,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
        );
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        return dialog;
    }

    public void setTvProgress(String progressTip) {
        tvProgress.setText(progressTip);
    }
}
