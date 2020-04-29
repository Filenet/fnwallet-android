package io.filenet.wallet.ui.contract;

import io.filenet.wallet.base.BaseContract;
import io.filenet.wallet.domain.ETHWallet;


public interface LoadWalletContract extends BaseContract {
    interface View extends BaseContract.BaseView {
        void loadSuccess(ETHWallet wallet) throws InterruptedException;

        void loadFail();

        void walletRepeat(ETHWallet mEthWallet);

        void mnemonicError();
    }


    interface Presenter extends BaseContract.BasePresenter {
        void loadWalletByMnemonic(String walletName, String bipPath, String mnemonic, String pwd);

        void loadWalletByKeystore(String keystore, String pwd, String walletName);

        void loadWalletByPrivateKey(String walletName, String privateKey, String pwd);
    }
}
