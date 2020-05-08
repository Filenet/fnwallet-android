package io.filenet.wallet.ui.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.domain.VerifyMnemonicWordTag;
import io.filenet.wallet.ui.adapter.VerifyBackupMnemonicWordsAdapter;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class VerifyMnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_SUCCESS_RESULT = 2202;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_selected)
    RecyclerView rvSelected;
    @BindView(R.id.rv_mnemonic)
    RecyclerView rvMnemonic;


    @BindView(R.id.btn_empty)
    TextView btnEmpty;

    private String walletMnemonic;
    private List<VerifyMnemonicWordTag> mnemonicWords;
    private List<String> selectedMnemonicWords;
    private VerifyBackupMnemonicWordsAdapter verifyBackupMenmonicWordsAdapter;


    MnemonicBackupActivity.MnemonicAdapter mnemonicAdapter;
    private long walletId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    public void initDatas() {
        walletId = getIntent().getLongExtra("walletId", -1);
        walletMnemonic = getIntent().getStringExtra("walletMnemonic");
        String[] words = walletMnemonic.split("\\s+");
        mnemonicWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            VerifyMnemonicWordTag verifyMnemonicWordTag = new VerifyMnemonicWordTag();
            verifyMnemonicWordTag.setMnemonicWord(words[i]);
            mnemonicWords.add(verifyMnemonicWordTag);
        }
        Collections.shuffle(mnemonicWords);
        rvMnemonic.setLayoutManager(new GridLayoutManager(this, 3));
        verifyBackupMenmonicWordsAdapter = new VerifyBackupMnemonicWordsAdapter(R.layout.list_item_mnemoic, mnemonicWords);
        rvMnemonic.setAdapter(verifyBackupMenmonicWordsAdapter);
        rvSelected.setLayoutManager(new GridLayoutManager(this, 3));
        selectedMnemonicWords = new ArrayList<>();
        mnemonicAdapter = new MnemonicBackupActivity.MnemonicAdapter(selectedMnemonicWords, this);
        rvSelected.setAdapter(mnemonicAdapter);
    }

    @Override
    public void configViews() {
        verifyBackupMenmonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                VerifyMnemonicWordTag verifyMnemonicWordTag = verifyBackupMenmonicWordsAdapter.getData().get(position);
                String mnemonicWord = verifyMnemonicWordTag.getMnemonicWord();
                if (verifyMnemonicWordTag.isSelected()) {
                    verifyMnemonicWordTag.setSelected(false);
                    for (int i = 0; i < mnemonicAdapter.getData().size(); i++) {
                        if (mnemonicWord.equals(mnemonicAdapter.getData().get(i))) {
                            mnemonicAdapter.remove(i);
                            mnemonicAdapter.notifyDataSetChanged();
                            verifyBackupMenmonicWordsAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    verifyMnemonicWordTag.setSelected(true);
                    mnemonicAdapter.addData(mnemonicWord);
                    mnemonicAdapter.notifyDataSetChanged();
                    verifyBackupMenmonicWordsAdapter.notifyDataSetChanged();
                }

            }
        });

        btnEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (VerifyMnemonicWordTag datum : verifyBackupMenmonicWordsAdapter.getData()) {
                    datum.setSelected(false);
                }
                verifyBackupMenmonicWordsAdapter.notifyDataSetChanged();
                mnemonicAdapter.getData().clear();
                mnemonicAdapter.notifyDataSetChanged();
            }
        });

    }

    @OnClick(R.id.btn_confirm)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                LogUtils.d("VerifyMnemonicBackUp", "Click!!");
                List<String> data = mnemonicAdapter.getData();
                int size = data.size();
                if (size == 12) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        stringBuilder.append(data.get(i));
                        if (i != size - 1) {
                            stringBuilder.append(" ");
                        }
                    }
                    String verifyMnemonic = stringBuilder.toString();
                    String trim = verifyMnemonic.trim();
                    LogUtils.d("VerifyMnemonicBackUp", "Click!!");
                    if (TextUtils.equals(trim, walletMnemonic.trim())) {
                        ETHWallet mWallet = WalletDaoUtils.setIsBackup(walletId);
                        ToastUtils.showToast(mContext, R.string.right_mnemonic);
                        EventBus.getDefault().post(mWallet);
                        setResult(VERIFY_SUCCESS_RESULT, new Intent(mContext, MainActivity.class));
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        VerifyMnemonicBackupActivity.this.finish();
                    } else {
                        ToastUtils.showToast(mContext, R.string.verify_backup_failed);
                    }
                } else {
                    ToastUtils.showToast(mContext, R.string.verify_backup_failed);
                }
                break;
        }
    }
}
