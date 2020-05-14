package io.filenet.wallet.ui.contract;

import io.filenet.wallet.base.BaseContract;
import io.filenet.wallet.domain.ETHWallet;


public interface WalletDetailContract extends BaseContract {
    interface View extends BaseView {
        void modifySuccess();

        void modifyPwdSuccess(ETHWallet wallet);

        void showDerivePrivateKeyDialog(String privateKey);

        void showDeriveKeystore(String keystore);

        void deleteSuccess(boolean isDelete);
    }

    interface Presenter extends BasePresenter {
        void modifyWalletName(long walletId, String name);

        void modifyWalletPwd(long walletId, String walletName, String oldPassword, String newPassword);

        void deriveWalletPrivateKey(long walletId, String password);

        void deriveWalletKeystore(long walletId, String password);

        void deleteWallet(long walletId);
    }
}
