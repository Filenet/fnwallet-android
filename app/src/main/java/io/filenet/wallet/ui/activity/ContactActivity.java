package io.filenet.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.ToastUtils;

public class ContactActivity extends BaseActivity {


    @BindView(R.id.lly_contact_web)
    LinearLayout llyContactWeb;
    @BindView(R.id.lly_contact_email)
    TextView llyContactEmail;
    @BindView(R.id.lly_contact_telegram)
    TextView llyContactTelegram;
    @BindView(R.id.lly_contact_twitter)
    TextView llyContactTwitter;
    @BindView(R.id.lly_contact_facebook)
    TextView llyContactFacebook;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.setLocal(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        llyContactWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://filenet.io");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        llyContactEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", "contact@filenet.io");
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;
            }
        });


        llyContactTelegram.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", "t.me/filenetcommunity");
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;

            }
        });

        llyContactTwitter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", "@Filenetglobal");
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;
            }
        });

        llyContactFacebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", "RC Filenet");
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;

            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_setting_contact;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 1f)
                .navigationBarColor(R.color.black)
                .init();
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        ivBack.setImageResource(R.mipmap.app_back_black);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        tvTitle.setText(getString(R.string.contact_us_title));

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
