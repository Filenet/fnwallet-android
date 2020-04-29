package io.filenet.wallet.ui.presenter;

import org.bitcoinj.crypto.MnemonicException;

import java.util.Arrays;

import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.contract.LoadWalletContract;
import io.filenet.wallet.utils.ETHWalletUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class LoadWalletPresenter implements LoadWalletContract.Presenter {
    public LoadWalletPresenter(LoadWalletContract.View mView) {
        this.mView = mView;
    }

    private LoadWalletContract.View mView;


    @Override
    public void loadWalletByMnemonic(String walletName, final String bipPath, final String mnemonic, final String pwd) {
        Observable.create((ObservableOnSubscribe<ETHWallet>) e -> {
            try {
                ETHWallet ethWallet = ETHWalletUtils.importMnemonic(bipPath
                        , Arrays.asList(mnemonic.split(" ")), pwd);
                if (ethWallet != null) {
                    if (WalletDaoUtils.walletAddressChecking(ethWallet.getAddress())) {
                        e.onError(new WalletRepeatException(ethWallet));
                    } else {
                        ethWallet.setName(walletName);
                        ethWallet.setBackup(true);
                        ethWallet.setCurrent(true);
                        WalletDaoUtils.insertNewWallet(ethWallet);
                        e.onNext(ethWallet);
                    }
                }
            } catch (MnemonicException e1) {
                e1.printStackTrace();
                e.onError(e1);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet wallet) {
                        try {
                            mView.loadSuccess(wallet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof MnemonicException) {
                            mView.loadFail();
                        } else if (e instanceof WalletRepeatException) {
                            mView.walletRepeat(((WalletRepeatException) e).mEthWallet);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void loadWalletByKeystore(final String keystore, final String pwd, String walletName) {
        Observable.create((ObservableOnSubscribe<ETHWallet>) e -> {
            ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, pwd);
            if (ethWallet != null) {
                if (WalletDaoUtils.walletAddressChecking(ethWallet.getAddress())) {
                    e.onError(new WalletRepeatException(ethWallet));
                } else {
                    ethWallet.setBackup(true);
                    ethWallet.setName(walletName);
                    ethWallet.setCurrent(true);
                    WalletDaoUtils.insertNewWallet(ethWallet);
                    e.onNext(ethWallet);
                }
            } else {
                e.onError(new Exception());
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet wallet) {
                        try {
                            mView.loadSuccess(wallet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof WalletRepeatException) {
                            mView.walletRepeat(((WalletRepeatException) e).mEthWallet);
                        } else {
                            mView.loadFail();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void loadWalletByPrivateKey(String walletName, final String privateKey, final String pwd) {
        Observable.create(new ObservableOnSubscribe<ETHWallet>() {
            @Override
            public void subscribe(ObservableEmitter<ETHWallet> e) throws Exception {
                ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(privateKey, pwd);
                if (ethWallet != null) {
                    if (WalletDaoUtils.walletAddressChecking(ethWallet.getAddress())) {
                        e.onError(new WalletRepeatException(ethWallet));
                    } else {
                        ethWallet.setName(walletName);
                        ethWallet.setBackup(true);
                        ethWallet.setCurrent(true);

                        WalletDaoUtils.insertNewWallet(ethWallet);
                        e.onNext(ethWallet);
                    }
                } else {
                    e.onError(new Exception());
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ETHWallet>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ETHWallet wallet) {
                        try {
                            mView.loadSuccess(wallet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof WalletRepeatException) {
                            mView.walletRepeat(((WalletRepeatException) e).mEthWallet);
                        } else {
                            mView.loadFail();
                        }
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public class WalletRepeatException extends Exception {

        private ETHWallet mEthWallet;

        public WalletRepeatException(ETHWallet mEthWallet) {
            this.mEthWallet = mEthWallet;
        }

        public ETHWallet getEthWallet() {
            return mEthWallet;
        }

        public void setEthWallet(ETHWallet mEthWallet) {
            this.mEthWallet = mEthWallet;
        }
    }
}
