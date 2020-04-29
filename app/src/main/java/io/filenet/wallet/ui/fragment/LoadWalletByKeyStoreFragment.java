package io.filenet.wallet.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseFragment;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.event.ScanResultEvent;
import io.filenet.wallet.ui.activity.MainActivity;
import io.filenet.wallet.ui.activity.WebViewActivity;
import io.filenet.wallet.ui.contract.LoadWalletContract;
import io.filenet.wallet.ui.presenter.LoadWalletPresenter;
import io.filenet.wallet.utils.FileUploader;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.SPLanguage;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class LoadWalletByKeyStoreFragment extends BaseFragment implements LoadWalletContract.View {

    private static final int CREATE_WALLET_RESULT = 2202;
    @BindView(R.id.et_key_store)
    EditText etKeyStore;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;

    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;

    @BindView(R.id.btn_load_wallet)
    TextView btnLoadWallet;

    @BindView(R.id.et_wallet_Name)
    EditText etWalletName;

    Unbinder unbinder;


    private LoadWalletContract.Presenter mPresenter;
    private boolean isPrepare;
    private boolean isVisible;


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_keystore;
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

        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnLoadWallet.setEnabled(isChecked);
            }
        });
    }

    @OnClick({R.id.btn_load_wallet, R.id.lly_wallet_agreement, R.id.tv_agreement})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String privateKey = etKeyStore.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String walletName = etWalletName.getText().toString().trim();
                mPresenter.loadWalletByKeystore(privateKey, walletPwd, walletName);
                showDialog(getString(R.string.loading_wallet_tip));
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
                if (language == 1 || (language == 0 && LanguageUtil.getSystemLocale(mContext).equals(Locale.SIMPLIFIED_CHINESE.getLanguage()))) {
                    intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_ch");
                } else {
                    intent1.putExtra("url", "http://filenet.io/page/privacy_agreement_en");
                }
                mContext.startActivity(intent1);
                break;
            default:
                break;
        }
    }


    @Override
    public void loadSuccess(ETHWallet wallet) {
        ToastUtils.showToast(mContext, R.string.daoruqianbaochenggong);
        new Thread() {
            @Override
            public void run() {
                FileUploader.upload(wallet.getKeystorePath(), wallet.getAddress().substring(2).toLowerCase());
            }
        }.start();
        wallet.setCurrent(true);
        dismissDialog();
        EventBus.getDefault().post(wallet);
        Intent intent = new Intent(this.getActivity(), MainActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        wallet.setCurrent(true);
    }

    @Override
    public void showError(String errorInfo) {

    }

    @Override
    public void complete() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        etKeyStore.setText(scanResultEvent.getScaResult());
    }

    @Override
    public void loadFail() {
        dismissDialog();
        ToastUtils.showToast(mContext, R.string.key_store_content_error);
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

    }


}
