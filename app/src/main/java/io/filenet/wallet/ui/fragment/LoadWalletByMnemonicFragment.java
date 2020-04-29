package io.filenet.wallet.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseFragment;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.event.LoadWalletSuccessEvent;
import io.filenet.wallet.event.ScanResultEvent;
import io.filenet.wallet.ui.activity.MainActivity;
import io.filenet.wallet.ui.activity.WebViewActivity;
import io.filenet.wallet.ui.contract.LoadWalletContract;
import io.filenet.wallet.ui.presenter.LoadWalletPresenter;
import io.filenet.wallet.utils.ETHWalletUtils;
import io.filenet.wallet.utils.FileUploader;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.SPLanguage;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.UUi;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.filenet.wallet.view.LoadWalletSelectStandardPopupWindow;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoadWalletByMnemonicFragment extends BaseFragment implements LoadWalletContract.View {

    @BindView(R.id.et_mnemonic)
    EditText etMnemonic;
    @BindView(R.id.et_standard)
    EditText etStandard;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;
    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;
    @BindView(R.id.lly_wallet_agreement)
    LinearLayout llyWalletAgreement;
    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;
    @BindView(R.id.et_wallet_Name)
    EditText etWalletName;
    Unbinder unbinder;
    private LoadWalletContract.Presenter mPresenter;
    private LoadWalletSelectStandardPopupWindow popupWindow;

    private boolean isPrepare;
    private boolean isVisible;

    private String ethType = ETHWalletUtils.ETH_JAXX_TYPE;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_mnemonic;
    }

    @Override
    public void attachView() {
        isPrepare = true;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
    }

    @Override
    public void initDatas() {
        mPresenter = new LoadWalletPresenter(this);
    }

    @Override
    public void configViews() {
        popupWindow = new LoadWalletSelectStandardPopupWindow(mContext);
        popupWindow.setOnPopupItemSelectedListener(new LoadWalletSelectStandardPopupWindow.OnPopupItemSelectedListener() {
            @Override
            public void onSelected(int selection) {
                switch (selection) {
                    case 0:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard);
                        ethType = ETHWalletUtils.ETH_JAXX_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 1:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_ledger);
                        ethType = ETHWalletUtils.ETH_LEDGER_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 2:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_custom);
                        ethType = ETHWalletUtils.ETH_CUSTOM_TYPE;
                        etStandard.setEnabled(true);
                        etStandard.setFocusable(true);
                        etStandard.setFocusableInTouchMode(true);
                        etStandard.requestFocus();
                        break;

                }
            }
        });
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnLoadWallet.setEnabled(isChecked);
            }
        });

    }

    @Override
    public void loadSuccess(ETHWallet wallet) {

        ToastUtils.showToast(mContext, R.string.daoruqianbaochenggong);
        new Thread() {
            @Override
            public void run() {
                FileUploader.upload(wallet.getKeystorePath(), wallet.getAddress().substring(2));
            }
        }.start();
        LoadWalletSuccessEvent loadWalletSuccessEvent = new LoadWalletSuccessEvent();
        loadWalletSuccessEvent.setEthWallet(wallet);
        EventBus.getDefault().post(loadWalletSuccessEvent);
        EventBus.getDefault().post(wallet);
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
        this.getActivity().finish();
    }

    @Override
    public void loadFail() {
        dismissDialog();
        ToastUtils.showToast(mContext, R.string.import_error);
    }

    @Override
    public void walletRepeat(ETHWallet mEthWallet) {
        new AlertDialog.Builder(getContext()).setMessage(R.string.setting_new_password_please_remember_new_password)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog("");
                        Observable.create((ObservableOnSubscribe<ETHWallet>) emitter -> {
                                    EventBus.getDefault().post(mEthWallet);
                                    WalletDaoUtils.ethWalletDao.save(mEthWallet);
                                    emitter.onNext(mEthWallet);
                                }
                        ).doOnNext(new Consumer<ETHWallet>() {
                            @Override
                            public void accept(ETHWallet ethWallet) throws Exception {
                                FileUploader.upload(ethWallet.getKeystorePath(), ethWallet.getAddress().substring(2));
                            }
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ETHWallet>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(ETHWallet ethWallet) {
                                        dismissDialog();
                                        ToastUtils.showToast(mContext, R.string.change_password_success);
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        dismissDialog();
                                    }

                                    @Override
                                    public void onComplete() {
                                        dismissDialog();
                                    }

                                });
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog();
                    }
                })
                .show();
    }

    @Override
    public void mnemonicError() {
        dismissDialog();
        ToastUtils.showToast(mContext, R.string.mnemonic_error);
    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {

    }

    @OnClick({R.id.btn_load_wallet, R.id.lly_standard_menu, R.id.lly_wallet_agreement, R.id.tv_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String mnemonic = etMnemonic.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                String walletName = etWalletName.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(walletName, mnemonic, walletPwd, confirmPwd, pwdReminder);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    mPresenter.loadWalletByMnemonic(walletName, ethType, mnemonic, walletPwd);
                }
                break;
            case R.id.lly_standard_menu:
                LogUtils.i("LoadWallet", "lly_standard_menu");
                popupWindow.showAsDropDown(etStandard, 0, UUi.dip2px(10));
                break;
            case R.id.cb_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                    btnLoadWallet.setEnabled(false);
                } else {
                    cbAgreement.setChecked(true);
                    btnLoadWallet.setEnabled(true);
                }
                break;
            case R.id.tv_agreement:
                Intent intent1 = new Intent(mContext, WebViewActivity.class);
                int language = SPLanguage.getInstance(mContext).getSelectLanguage();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    if (language == 1 || (language == 0 && LanguageUtil.getSystemLocale(mContext).equals(Locale.SIMPLIFIED_CHINESE.getLanguage()))) {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_ch");
                    } else {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_en");
                    }
                } else {
                    if (language == 1 || (language == 0 && LanguageUtil.getSystemLocale(mContext).equals(Locale.CHINESE))) {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_ch");
                    } else {
                        intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_en");
                    }
                }
                mContext.startActivity(intent1);
                break;
            default:
                break;
        }
    }

    private boolean verifyInfo(String walletName, String mnemonic, String walletPwd, String confirmPwd, String pwdReminder) {

        if (TextUtils.isEmpty(mnemonic)) {
            ToastUtils.showToast(mContext, R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_input_tips);
            return false;
        } else if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_name_repeat_tips);
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        } else if (walletPwd.length() < 7 | walletPwd.length() < 7) {
            ToastUtils.showToast(mContext, R.string.create_wallet_pwd_lenght_input_tips);
            return false;
        } else if (Arrays.asList(mnemonic.split(" ")).size() != 12) {
            ToastUtils.showToast(mContext, R.string.create_wallet_mnemeric);
            return false;
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        wallet.setCurrent(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ScanResultEvent(ScanResultEvent scanResultEvent) {
        etMnemonic.setText(scanResultEvent.getScaResult());
    }
}
