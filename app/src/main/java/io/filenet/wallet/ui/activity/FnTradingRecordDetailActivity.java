package io.filenet.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.utils.Base58;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;

public class FnTradingRecordDetailActivity extends BaseActivity {

    @BindView(R.id.fndetailvalue)
    TextView fndetailvalue;
    @BindView(R.id.fndetailaddressFrom)
    TextView fndetailaddressFrom;
    @BindView(R.id.fndetailaddressTo)
    TextView fndetailaddressTo;
    @BindView(R.id.fntrdetailblockNum)
    TextView fntrdetailblockNum;
    @BindView(R.id.fntrdetailhashNum)
    TextView fntrdetailhashNum;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.iv_back)
    ImageView mIvBack;

    @BindView(R.id.fnTrDetailBlockTime)
    TextView fnTrDetailBlockTime;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fn_trading_record_detail;
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
        tvTitle.setText(getString(R.string.translation_detail));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mIvBack.setImageResource(R.mipmap.app_back_black);
        ImmersionBar.with(this)
                .navigationBarColor(R.color.black)
                .transparentStatusBar()
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 1f)
                .init();

        Intent intent = getIntent();
        ETHWallet wallet = WalletDaoUtils.getCurrent();
        String walletAddress = wallet.getAddress();
        String value = String.valueOf(intent.getLongExtra("value", 0));

        if (intent.getStringExtra("from").equals(Base58.encode(walletAddress.toLowerCase().substring(2, walletAddress.length()).getBytes()))) {
            fndetailvalue.setTextColor(ContextCompat.getColor(this, R.color.color_FF6677));
            fndetailvalue.setText("-" + new BigDecimal(value).divide(new BigDecimal("1000000000")).toString() + "Fn");

        } else {
            fndetailvalue.setTextColor(ContextCompat.getColor(this, R.color.color_7ED321));
            fndetailvalue.setText("+" + new BigDecimal(value).divide(new BigDecimal("1000000000")).toString() + "Fn");

        }
        fndetailaddressFrom.setText(intent.getStringExtra("from"));
        fndetailaddressTo.setText(intent.getStringExtra("to"));
        fntrdetailblockNum.setText(intent.getStringExtra("height"));
        fntrdetailhashNum.setText(intent.getStringExtra("hash"));
        fnTrDetailBlockTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(intent.getIntExtra("timestamp", 0) - 8 * 3600 + "000"))) + "(UTC)");
    }

    @OnClick({R.id.fntrdetailblockNum, R.id.fntrdetailhashNum, R.id.fndetailaddressFrom, R.id.fndetailaddressTo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fntrdetailblockNum:

                fntrdetailblockNum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            ClipData mClipData = ClipData.newPlainText("Label", fntrdetailblockNum.getText());
                            cm.setPrimaryClip(mClipData);
                        }
                        ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                        return true;
                    }
                });
                break;
            case R.id.fntrdetailhashNum:
                fntrdetailhashNum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            ClipData mClipData = ClipData.newPlainText("Label", fntrdetailhashNum.getText());
                            cm.setPrimaryClip(mClipData);
                        }
                        ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                        return true;
                    }
                });
                break;
            case R.id.fndetailaddressTo:
                fndetailaddressTo.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            ClipData mClipData = ClipData.newPlainText("Label", fndetailaddressTo.getText());
                            cm.setPrimaryClip(mClipData);
                        }
                        ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                        return true;
                    }
                });
                break;
            case R.id.fndetailaddressFrom:
                fndetailaddressFrom.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            ClipData mClipData = ClipData.newPlainText("Label", fndetailaddressFrom.getText());
                            cm.setPrimaryClip(mClipData);
                        }
                        ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                        return true;
                    }
                });
                break;
        }
    }
}
