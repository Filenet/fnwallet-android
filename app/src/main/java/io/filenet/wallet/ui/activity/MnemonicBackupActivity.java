package io.filenet.wallet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.utils.AppManager;
import io.filenet.wallet.view.MnemonicBackupAlertDialog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_MNEMONIC_BACKUP_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_mnemonic)
    TextView tvMnemonic;


    @BindView(R.id.mnemonic_rv)
    RecyclerView RvMnemonic;
    private String walletMnemonic;
    private long walletId;

    private boolean isFirstInit = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
    }

    @Override
    public void initDatas() {
        AppManager.getAppManager().addActivity(this);
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        String[] words = walletMnemonic.split("\\s+");
        List<String> mnemonicList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            mnemonicList.add(words[i]);
        }
        MnemonicAdapter mnemonicAdapter = new MnemonicAdapter(mnemonicList, this);
        RvMnemonic.setLayoutManager(new GridLayoutManager(this, 3));
        RvMnemonic.setAdapter(mnemonicAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstInit) {
            isFirstInit = false;
            Observable<Long> longObservable = Observable.timer(500, TimeUnit.MILLISECONDS);
            longObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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
                            MnemonicBackupAlertDialog mnemonicBackupAlertDialog = new MnemonicBackupAlertDialog(mContext);
                            mnemonicBackupAlertDialog.show();
                        }
                    });
        }

    }

    @Override
    public void configViews() {
        ivBack.setImageResource(R.mipmap.app_back_black);
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));

        ImmersionBar.with(this)
                .navigationBarColor(R.color.black)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
    }

    @OnClick(R.id.btn_next)
    public void onClick(View view) {
        Intent intent = new Intent(this, VerifyMnemonicBackupActivity.class);
        intent.putExtra("walletId", walletId);
        intent.putExtra("walletMnemonic", walletMnemonic);
        startActivityForResult(intent, VERIFY_MNEMONIC_BACKUP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_MNEMONIC_BACKUP_REQUEST) {
            if (data != null) {
                finish();
            }
        }
    }

    public static class MnemonicAdapter extends RecyclerView.Adapter<MnemoincVH> {

        private List<String> mnemonicList;
        private Context mContext;


        public MnemonicAdapter(List<String> mnemonicList, Context mContext) {
            this.mnemonicList = mnemonicList;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public MnemoincVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View mView = LayoutInflater.from(mContext).inflate(R.layout.layout_mnemonic, viewGroup, false);
            return new MnemoincVH(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull MnemoincVH mnemoincVH, int i) {
            mnemoincVH.tvString.setText(mnemonicList.get(i));
        }

        @Override
        public int getItemCount() {
            return mnemonicList.size();
        }

        public List<String> getData() {
            return mnemonicList;
        }

        public void remove(int i) {
            mnemonicList.remove(i);
        }

        public void addData(String mnemonicWord) {
            mnemonicList.add(mnemonicWord);
        }
    }

    public static class MnemoincVH extends RecyclerView.ViewHolder {

        public TextView tvString;

        public MnemoincVH(@NonNull View itemView) {
            super(itemView);
            tvString = itemView.findViewById(R.id.tv_mnemonic);
        }
    }

}


