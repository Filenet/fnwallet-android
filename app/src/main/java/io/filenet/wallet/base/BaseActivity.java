package io.filenet.wallet.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.gyf.barlibrary.ImmersionBar;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.filenet.wallet.R;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.view.loadding.CustomDialog;

public abstract class BaseActivity extends AppCompatActivity {

    public Toolbar mCommonToolbar;

    protected Context mContext;
    protected int statusBarColor = 0;
    public CustomDialog dialog;
    private Unbinder unbinder;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.setLocal(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        ImmersionBar.with(this).init();
        unbinder = ButterKnife.bind(this);
        mCommonToolbar = ButterKnife.findById(this, R.id.common_toolbar);
        if (mCommonToolbar != null) {
            ImmersionBar.with(this)
                    .titleBar(mCommonToolbar, false)
                    .transparentStatusBar()
                    .statusBarDarkFont(true, 1f)
                    .navigationBarColor(R.color.black)
                    .init();
            initToolBar();
            setSupportActionBar(mCommonToolbar);
        }
        LinearLayout llyBack = (LinearLayout) findViewById(R.id.lly_back);
        if (llyBack != null) {
            llyBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        initDatas();
        configViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSoftInputView();
        MobclickAgent.onPause(this);
    }

    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        ImmersionBar.with(this).destroy();
        dismissDialog();
    }

    public abstract int getLayoutId();

    public abstract void initToolBar();

    public abstract void initDatas();

    public abstract void configViews();

    protected void gone(final View... views) {
        setViewVisibility(false, views);
    }

    protected void visible(final View... views) {
        setViewVisibility(true, views);
    }

    private void setViewVisibility(boolean showViews, View... views){
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    if (showViews)
                        view.setVisibility(View.VISIBLE);
                    else
                        view.setVisibility(View.GONE);
                }
            }
        }
    }

    public CustomDialog getDialog() {
        if (dialog == null) {
            dialog = CustomDialog.instance(this);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.hide();
        }
    }

    public void showDialog(String progressTip) {
        getDialog().show();
        if (progressTip != null) {
            getDialog().setTvProgress(progressTip);
        }
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public int getCompatColor(@ColorRes int color) {
        return ContextCompat.getColor(this, color);
    }


}
