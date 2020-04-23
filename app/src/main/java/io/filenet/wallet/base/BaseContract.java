package io.filenet.wallet.base;


public interface BaseContract {

    interface BasePresenter {
    }

    interface BaseView {

        void showError(String errorInfo);

        void complete();

    }
}
