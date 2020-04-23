package io.filenet.wallet.ui.presenter;


import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.List;

import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.contract.PropertyContract;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class PropertyPresenter implements PropertyContract.Presenter {
    private PropertyContract.View mView;

    public PropertyPresenter(PropertyContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void refreshWallet() {

    }

    @Override
    public void switchCurrentWallet(final int position, final long walletId) {
        Observable.create(new ObservableOnSubscribe<ETHWallet>() {
            @Override
            public void subscribe(ObservableEmitter<ETHWallet> e) throws Exception {
                e.onNext(WalletDaoUtils.updateCurrent(walletId));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet wallet) {
                        mView.switchWallet(position, wallet);
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
    public void loadAllWallets() {
        Observable.create(new ObservableOnSubscribe<List<ETHWallet>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ETHWallet>> e) throws Exception {
                e.onNext(WalletDaoUtils.loadAll());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ETHWallet>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ETHWallet> ethWallets) {
                        mView.showDrawerWallets(ethWallets);
                        for (ETHWallet ethwallet : ethWallets) {
                            if (ethwallet.getIsCurrent()) {
                                mView.showWallet(ethwallet);
                            }
                        }
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
    public void selectBalance(final String walletAddress) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                String balance = null;
                Admin web3j = AdminFactory.build(new HttpService());
                DefaultBlockParameter blockParameter = new DefaultBlockParameter() {
                    @Override
                    public String getValue() {
                        return null;
                    }
                };

                try {

                    balance = web3j.ethGetBalance(walletAddress, blockParameter).send().getBalance().toString();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
                e.onNext(balance);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String balance) {
                        mView.showCurrentWalletProrperty(balance);
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
