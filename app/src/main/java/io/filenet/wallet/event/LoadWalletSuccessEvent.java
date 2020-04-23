package io.filenet.wallet.event;

import io.filenet.wallet.domain.ETHWallet;


public class LoadWalletSuccessEvent {

    public ETHWallet getEthWallet() {
        return ethWallet;
    }

    public void setEthWallet(ETHWallet ethWallet) {
        this.ethWallet = ethWallet;
    }

    private ETHWallet ethWallet;
}
