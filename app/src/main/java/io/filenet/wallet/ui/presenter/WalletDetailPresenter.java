package io.filenet.wallet.ui.presenter;

import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.contract.WalletDetailContract;
import io.filenet.wallet.utils.ETHWalletUtils;
import io.filenet.wallet.utils.FileUploader;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class WalletDetailPresenter implements WalletDetailContract.Presenter {

    private WalletDetailContract.View mView;

    public WalletDetailPresenter(WalletDetailContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void modifyWalletName(long walletId, String name) {
        WalletDaoUtils.updateWalletName(walletId, name);
        mView.modifySuccess();
    }

    @Override
    public void modifyWalletPwd(final long walletId, final String walletName, final String oldPassword, final String newPassword) {
        Observable.create(new ObservableOnSubscribe<ETHWallet>() {
            @Override
            public void subscribe(ObservableEmitter<ETHWallet> e) throws Exception {
                e.onNext(ETHWalletUtils.modifyPassword(walletId, walletName,oldPassword, newPassword));
                e.onComplete();
            }
        }).flatMap(new Function<ETHWallet, ObservableSource<ETHWallet>>() {
            @Override
            public ObservableSource<ETHWallet> apply(ETHWallet ethWallet) throws Exception {
                Observable.create((ObservableOnSubscribe<ETHWallet>) emitter -> {
                    FileUploader.upload(ethWallet.getKeystorePath(), ethWallet.getAddress().substring(2));
                    emitter.onComplete();
                });
                return Observable.just(ethWallet);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet ethWallet) {
                        mView.modifyPwdSuccess(ethWallet);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showError("");
                    }

                    @Override
                    public void onComplete() {
                        mView.complete();
                    }
                });
    }

    @Override
    public void deriveWalletPrivateKey(final long walletId, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(ETHWalletUtils.derivePrivateKey(walletId, password));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String privateKey) {
                        mView.showDerivePrivateKeyDialog(privateKey);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void deriveWalletKeystore(final long walletId, final String password) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext(ETHWalletUtils.deriveKeystore(walletId, password));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String keystore) {
                        mView.showDeriveKeystore(keystore);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void deleteWallet(final long walletId) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                e.onNext(ETHWalletUtils.deleteWallet(walletId));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean isDelete) {
                        mView.deleteSuccess(isDelete);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
