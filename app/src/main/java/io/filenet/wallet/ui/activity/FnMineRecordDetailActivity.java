package io.filenet.wallet.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.entity.MinEarnBean;
import io.filenet.wallet.utils.ToastUtils;

public class FnMineRecordDetailActivity extends BaseActivity {

    @BindView(R.id.reward)
    TextView reward;
    @BindView(R.id.prevHash)
    TextView prevHash;
    @BindView(R.id.hash)
    TextView hash;
    @BindView(R.id.height)
    TextView height;
    @BindView(R.id.transactions)
    TextView transactions;
    @BindView(R.id.timestamp)
    TextView timestamp;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fn_mine_record_detail;
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {

        Intent intent = getIntent();
        MinEarnBean.DataBean.BlocksBean blocksBean = (MinEarnBean.DataBean.BlocksBean) intent.getSerializableExtra("data");
        reward.setText(new BigDecimal(blocksBean.getReward()).divide(new BigDecimal("1000000000")).toString() + "Fn");
        prevHash.setText(blocksBean.getPrevHash());
        hash.setText(blocksBean.getHash());
        height.setText(blocksBean.getHeight());
        transactions.setText(blocksBean.getMiner());
        timestamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(blocksBean.getTimestamp() - 8 * 3600 + "000"))) + "(UTC)");

        hash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", hash.getText());
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;
            }
        });
        prevHash.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) {
                    ClipData mClipData = ClipData.newPlainText("Label", prevHash.getText());
                    cm.setPrimaryClip(mClipData);
                }
                ToastUtils.showToast(mContext, R.string.gathering_qrcode_copy_success);
                return true;
            }
        });
    }
}
