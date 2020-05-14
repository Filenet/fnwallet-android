package io.filenet.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.event.DeleteWalletSuccessEvent;
import io.filenet.wallet.event.LoadWalletSuccessEvent;
import io.filenet.wallet.ui.contract.WalletDetailContract;
import io.filenet.wallet.ui.presenter.WalletDetailPresenter;
import io.filenet.wallet.utils.Md5Utils;
import io.filenet.wallet.utils.SharedPreferencesUtil;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.filenet.wallet.view.InputPwdDialog;
import io.filenet.wallet.view.InputWalletNameDialog;
import io.filenet.wallet.view.PrivateKeyDeriveDialog;


public class WalletDetailActivity extends BaseActivity implements WalletDetailContract.View {

    private static final int WALLET_DETAIL_RESULT = 2201;
    private static final int MODIFY_PASSWORD_REQUEST = 1102;
    @BindView(R.id.lly_back)
    LinearLayout llyBack;
    @BindView(R.id.iv_btn)
    TextView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.civ_wallet)
    CircleImageView civWallet;
    @BindView(R.id.tv_eth_balance)
    TextView tvEthBalance;
    @BindView(R.id.lly_wallet_property)
    LinearLayout llyWalletProperty;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.et_wallet_name)
    TextView etWalletName;
    @BindView(R.id.tv_total_assets)
    TextView tvTotalAssets;
    @BindView(R.id.cv_fn)
    CardView cvFn;
    @BindView(R.id.rl_change_wallet_name)
    RelativeLayout changeWalletName;
    @BindView(R.id.rl_modify_pwd)
    RelativeLayout rlModifyPwd;
    @BindView(R.id.rl_derive_private_key)
    RelativeLayout rlDerivePrivateKey;
    @BindView(R.id.rl_derive_keystore)
    RelativeLayout rlDeriveKeystore;
    @BindView(R.id.rl_derive_nmic)
    RelativeLayout rlDeriveNmic;
    @BindView(R.id.btn_delete_wallet)
    TextView btnDeleteWallet;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    private long walletId;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private boolean walletIsBackup;
    private WalletDetailContract.Presenter mPresenter;
    private InputPwdDialog inputPwdDialog;
    private InputWalletNameDialog inputWalletNameDialog;
    private String walletMnemonic;
    private PrivateKeyDeriveDialog privateKeyDeriveDialog;
    private boolean fromList;
    private String fnTotal = String.format("%.02f", 0f);


    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        mPresenter = new WalletDetailPresenter(this);
        inputPwdDialog = new InputPwdDialog(this);
        inputWalletNameDialog = new InputWalletNameDialog(this);
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletPwd = intent.getStringExtra("walletPwd");
        walletAddress = intent.getStringExtra("walletAddress");
        walletName = intent.getStringExtra("walletName");
        walletIsBackup = intent.getBooleanExtra("walletIsBackup", false);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        fromList = intent.getBooleanExtra("fromList", false);
        fnTotal = intent.getStringExtra("fn_total");
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .statusBarColor(R.color.white)
                .fitsSystemWindows(true)
                .init();
        tvTitle.setText(R.string.mine_wallet_manage);
        int currencyUnit = SharedPreferencesUtil.getInstance().getInt("currencyUnit", 0);
        if (currencyUnit == 0) {
            tvTotalAssets.setText(getString(R.string.property_total_assets) + "($)");
        } else {
            tvTotalAssets.setText(getString(R.string.property_total_assets) + "(¥)");
        }
        tvTotal.setText(fnTotal);
        etWalletName.setText(walletName);
        tvWalletAddress.setText(walletAddress);
        if (TextUtils.isEmpty(walletMnemonic)) {
            rlDeriveNmic.setVisibility(View.GONE);
        } else {
            rlDeriveNmic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void modifySuccess() {

    }

    @Override
    public void modifyPwdSuccess(ETHWallet wallet) {

    }

    @Override
    public void showDerivePrivateKeyDialog(String privateKey) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        }, 1000);
        privateKeyDeriveDialog = new PrivateKeyDeriveDialog(WalletDetailActivity.this);
        privateKeyDeriveDialog.show();
        privateKeyDeriveDialog.setPrivateKey(privateKey);
    }

    @Override
    public void showDeriveKeystore(String keystore) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        }, 1000);
        Intent intent = new Intent(this, DeriveKeystoreActivity.class);
        intent.putExtra("walletKeystore", keystore);
        startActivity(intent);
    }

    @Override
    public void deleteSuccess(boolean isDelete) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissDialog();
            }
        }, 2000);
        ToastUtils.showToast(mContext, R.string.wallet_delete_success);
        if (fromList) {
            setResult(WALLET_DETAIL_RESULT, new Intent());
            EventBus.getDefault().post(new DeleteWalletSuccessEvent());
        } else {
            EventBus.getDefault().post(new DeleteWalletSuccessEvent());
            EventBus.getDefault().post(new LoadWalletSuccessEvent());
        }
        finish();
    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {

    }

    @OnClick({R.id.rl_btn, R.id.rl_modify_pwd, R.id.rl_change_wallet_name, R.id.btn_delete_wallet, R.id.rl_derive_private_key, R.id.rl_derive_keystore, R.id.rl_derive_nmic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                String name = etWalletName.getText().toString().trim();
                if (!TextUtils.equals(this.walletName, name) && verifyInfo(name)) {
                    mPresenter.modifyWalletName(walletId, name);
                    showDialog(getString(R.string.saving_wallet_tip));
                    new Handler().postDelayed(() -> {
                        dismissDialog();
                        ToastUtils.showToast(mContext, R.string.wallet_detail_save_success);
                        setResult(WALLET_DETAIL_RESULT, new Intent());
                        finish();
                    }, 1000);
                }
                break;
            case R.id.rl_modify_pwd:
                Intent intent = new Intent(mContext, ModifyPasswordActivity.class);
                intent.putExtra("walletId", walletId);
                intent.putExtra("walletPwd", walletPwd);
                intent.putExtra("walletAddress", walletAddress);
                intent.putExtra("walletName", walletName);
                intent.putExtra("walletIsBackup", walletIsBackup);
                startActivityForResult(intent, MODIFY_PASSWORD_REQUEST);
                break;
            case R.id.rl_derive_private_key:
                inputPwdDialog.show();
                inputPwdDialog.setDialogTitle(getString(R.string.wallet_detail_derive_private_key));
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            mPresenter.deriveWalletPrivateKey(walletId, pwd);
                        } else {
                            ToastUtils.showToast(mContext, R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.rl_derive_keystore:
                inputPwdDialog.show();
                inputPwdDialog.setDialogTitle(getString(R.string.wallet_detail_derive_keystore));
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            mPresenter.deriveWalletKeystore(walletId, pwd);
                        } else {
                            dismissDialog();
                            ToastUtils.showToast(mContext, R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.rl_derive_nmic:
                inputPwdDialog.show();
                inputPwdDialog.setDialogTitle(getString(R.string.wallet_detail_derive_nmic));
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            Intent intent = new Intent(WalletDetailActivity.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletId", walletId);
                            intent.putExtra("walletMnemonic", walletMnemonic);
                            startActivity(intent);
                            inputPwdDialog.dismiss();
                        } else {
                            ToastUtils.showToast(mContext, R.string.wallet_detail_wrong_pwd);
                        }

                    }
                });
                break;

            case R.id.btn_delete_wallet:
                inputPwdDialog.show();
                inputPwdDialog.setDialogTitle(getString(R.string.input_pwd_delete_tip));
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deleting_wallet_tip));
                            mPresenter.deleteWallet(walletId);
                        } else {
                            ToastUtils.showToast(mContext, R.string.wallet_detail_wrong_pwd);
                            inputPwdDialog.dismiss();
                        }
                    }
                });
                break;
            case R.id.rl_change_wallet_name:
                inputWalletNameDialog.show();
                inputWalletNameDialog.setDeleteAlertVisibility(false);
                inputWalletNameDialog.setOnInputDialogButtonClickListener(new InputWalletNameDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputWalletNameDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String name) {
                        if (!verifyInfo(name)) {
                            return;
                        }
                        if (!TextUtils.equals(walletName, name)) {
                            mPresenter.modifyWalletName(walletId, name);
                            inputWalletNameDialog.dismiss();
                            showDialog(getString(R.string.saving_wallet_tip));
                            new Handler().postDelayed(() -> {
                                dismissDialog();
                                ToastUtils.showToast(mContext, R.string.wallet_detail_save_success);
                                setResult(WALLET_DETAIL_RESULT, new Intent());
                                finish();
                            }, 2000);
                        }

                    }
                });
                break;
            default:
                break;
        }

    }

    private boolean verifyInfo(String walletName) {
        if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_repeat_tips);
            return false;
        } else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_input_tips);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_PASSWORD_REQUEST) {
            if (data != null) {
                walletPwd = data.getStringExtra("newPwd");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (inputPwdDialog != null) {
            inputPwdDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
