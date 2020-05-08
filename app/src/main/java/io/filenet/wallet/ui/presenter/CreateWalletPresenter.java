package io.filenet.wallet.ui.presenter;

import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.contract.CreateWalletContract;
import io.filenet.wallet.utils.ETHWalletUtils;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class CreateWalletPresenter implements CreateWalletContract.Presenter {

    public CreateWalletPresenter(CreateWalletContract.View mView) {
        this.mView = mView;
    }

    private CreateWalletContract.View mView;

    @Override
    public void createWallet(final String name, final String pwd, String confirmPwd, String pwdReminder) {
        Observable.create(new ObservableOnSubscribe<ETHWallet>() {
            @Override
            public void subscribe(ObservableEmitter<ETHWallet> e) throws Exception {
                ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(name, pwd);
                ethWallet.setName(name);
                ethWallet.setCurrent(false);
                WalletDaoUtils.insertNewWallet(ethWallet);
                e.onNext(ethWallet);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet wallet) {
                        mView.jumpToWalletBackUp(wallet);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("钱包创建失败");
                        LogUtils.e("CreateWalletPresenter", e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public boolean walletNameRepeatChecking(final String name) {
        return false;
    }

}
