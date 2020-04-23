package io.filenet.wallet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.filenet.wallet.R;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.SharedPreferencesUtil;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class StartActivity extends AppCompatActivity {

    private boolean isFirstUse;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.setLocal(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = LanguageUtil.getSetLanguageLocale(this.getApplicationContext());
        if (Locale.ENGLISH.equals(locale)){
            setContentView(R.layout.activity_start_en);
        }else {
            setContentView(R.layout.activity_start);
        }

        isFirstUse = SharedPreferencesUtil.getInstance().getBoolean("isFirstUse", true);
        Observable.timer(1500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Intent intent;
                        if (!isFirstUse) {
                            if (WalletDaoUtils.getCurrent() != null)
                                intent = new Intent(StartActivity.this, MainActivity.class);
                            else
                                intent = new Intent(StartActivity.this, FirstGuideActivity.class);
                        } else
                            intent = new Intent(StartActivity.this, FirstGuideActivity.class);

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

}
