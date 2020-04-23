package io.filenet.wallet.ui.contract;

import java.util.List;

import io.filenet.wallet.base.BaseContract;
import io.filenet.wallet.domain.ETHWallet;


public interface PropertyContract extends BaseContract {
    interface View extends BaseContract.BaseView {
        void showWallet(ETHWallet wallet);

        void switchWallet(int currentPosition, ETHWallet wallet);

        void showDrawerWallets(List<ETHWallet> ethWallets);

        void showCurrentWalletProrperty(String balance);
    }

    interface Presenter extends BaseContract.BasePresenter {
        void refreshWallet();

        void switchCurrentWallet(int position, long walletId);

        void loadAllWallets();

        void selectBalance(String walletAddress);
    }
}
