package io.filenet.wallet.ui.contract;

import io.filenet.wallet.base.BaseContract;
import io.filenet.wallet.domain.ETHWallet;



public interface CreateWalletContract extends BaseContract {

    interface View extends BaseContract.BaseView {
        void jumpToWalletBackUp(ETHWallet wallet);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void createWallet(String name, String pwd, String confirmPwd, String pwdReminder);

        boolean walletNameRepeatChecking(String name);
    }
}
