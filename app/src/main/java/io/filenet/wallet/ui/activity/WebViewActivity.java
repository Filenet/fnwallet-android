package io.filenet.wallet.ui.activity;

import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import butterknife.BindView;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.webview)
    WebView webView;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setBackgroundColor(getCompatColor(R.color.white));
        tvTitle.setText(R.string.create_wallet_agreement);
        tvTitle.setTextColor(getCompatColor(R.color.black));
    }

    @Override
    public void initDatas() {
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);

        }


    }

    @Override
    public void configViews() {

    }
}
