package io.filenet.wallet.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.contract.CreateWalletContract;
import io.filenet.wallet.ui.presenter.CreateWalletPresenter;
import io.filenet.wallet.utils.FileUploader;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.SPLanguage;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.functions.Consumer;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;


public class CreateWalletActivity extends BaseActivity implements CreateWalletContract.View {

    private static final int CREATE_WALLET_RESULT = 2202;
    private static final int LOAD_WALLET_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;
    @BindView(R.id.btn_create_wallet)
    TextView btnCreateWallet;

    @BindView(R.id.tv_create_tip)
    TextView tvCreateTip;


    private CreateWalletContract.Presenter mPresenter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("");
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        ivBack.setImageResource(R.mipmap.app_back_black);
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }


    @Override
    public void initDatas() {
        mPresenter = new CreateWalletPresenter(this);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .navigationBarColor(R.color.black)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnCreateWallet.setEnabled(isChecked);
            }
        });
        String notes = getString(R.string.notes);
        String createNotes = getString(R.string.create_wallet_notes);
        SpannableString spannableString = new SpannableString(notes + createNotes);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.note_tips)), 0, notes.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCreateTip.setText(spannableString);
    }

    @Override

    protected void onResume() {
        super.onResume();

    }

    @OnClick({R.id.tv_agreement, R.id.btn_create_wallet
            , R.id.lly_wallet_agreement, R.id.btn_input_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_agreement:
                Intent intent1 = new Intent(this, WebViewActivity.class);
                int language = SPLanguage.getInstance(this).getSelectLanguage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (language == 1 || (language == 0 && LanguageUtil.getSystemLocale(mContext).getLanguage().equals(Locale.SIMPLIFIED_CHINESE.getLanguage()))) {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_ch");
                    } else {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_en");
                    }
                } else {
                    if (language == 1 || (language == 0 && LanguageUtil.getSystemLocale(mContext).equals(Locale.SIMPLIFIED_CHINESE))) {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_ch");
                    } else {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_en");
                    }
                }
                startActivity(intent1);
                break;
            case R.id.btn_create_wallet:
                new RxPermissions(this)
                        .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    String walletName = etWalletName.getText().toString().trim();
                                    String walletPwd = etWalletPwd.getText().toString().trim();
                                    String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                                    String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                                    boolean verifyWalletInfo = verifyInfo(walletName, walletPwd, confirmPwd, pwdReminder);
                                    if (verifyWalletInfo) {
                                        showDialog(getString(R.string.creating_wallet_tip));
                                        mPresenter.createWallet(walletName, walletPwd, confirmPwd, pwdReminder);
                                    }
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    new AlertDialog.Builder(CreateWalletActivity.this)
                                            .setMessage(R.string.allow_storage_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //引导用户至设置页手动授权
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                } else {
                                    new AlertDialog.Builder(CreateWalletActivity.this)
                                            .setTitle(R.string.string_apply)
                                            .setMessage(R.string.allow_storage_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //引导用户至设置页手动授权
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                }
                            }
                        });


                break;
            case R.id.cb_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                } else {
                    cbAgreement.setChecked(true);
                }
                break;

            case R.id.btn_input_wallet:
                Intent intent = new Intent(this, LoadWalletActivity.class);
                startActivityForResult(intent, LOAD_WALLET_REQUEST);
                break;
            default:
                break;
        }
    }

    private boolean verifyInfo(String walletName, String walletPwd, String confirmPwd, String pwdReminder) {
        if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_repeat_tips);
            return false;
        } else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_input_tips);
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_input_tips);
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        } else if (walletPwd.length() < 8 || confirmPwd.length() < 8) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_lenght_input_tips);
            return false;
        }
        return true;
    }


    @Override
    public void showError(String errorInfo) {
        dismissDialog();
        LogUtils.e("CreateWalletActivity", errorInfo);
        ToastUtils.showToast(errorInfo);
    }

    @Override
    public void complete() {

    }

    @Override
    public void jumpToWalletBackUp(ETHWallet wallet) {
        ToastUtils.showToast(mContext, R.string.chuangjianqianbaochenggong);
        new Thread() {
            @Override
            public void run() {
                FileUploader.upload(wallet.getKeystorePath(), wallet.getAddress().substring(2));
            }
        }.start();
        dismissDialog();
        setResult(CREATE_WALLET_RESULT, new Intent());
        Intent intent = new Intent(this, MnemonicBackupActivity.class);
        intent.putExtra("walletId", wallet.getId());
        intent.putExtra("walletPwd", wallet.getPassword());
        intent.putExtra("walletAddress", wallet.getAddress());
        intent.putExtra("walletName", wallet.getName());
        intent.putExtra("walletMnemonic", wallet.getMnemonic());
        startActivity(intent);
//        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        finish();
    }
}
