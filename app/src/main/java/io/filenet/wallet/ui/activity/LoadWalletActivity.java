package io.filenet.wallet.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.base.BaseFragment;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.event.ScanResultEvent;
import io.filenet.wallet.ui.adapter.LoadWalletPageFragmentAdapter;
import io.filenet.wallet.ui.fragment.LoadWalletByKeyStoreFragment;
import io.filenet.wallet.ui.fragment.LoadWalletByMnemonicFragment;
import io.filenet.wallet.ui.fragment.LoadWalletByPrivateKeyFragment;
import io.filenet.wallet.utils.UUi;
import io.reactivex.functions.Consumer;


public class LoadWalletActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @BindView(R.id.indicator_view)
    ScrollIndicatorView indicatorView;
    @BindView(R.id.vp_load_wallet)
    ViewPager vpLoadWallet;

    protected Context mContext;

    private static final int QRCODE_SCANNER_REQUEST = 1100;

    private List<BaseFragment> fragmentList = new ArrayList<>();
    private LoadWalletPageFragmentAdapter loadWalletPageFragmentAdapter;
    private IndicatorViewPager indicatorViewPager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_load_wallet;
    }

    @Override
    public void initToolBar() {
        ivBtn.setImageResource(R.mipmap.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
        ivBack.setImageResource(R.mipmap.app_back_black);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void initDatas() {
        int type = getIntent().getIntExtra("import_type", 1);
        if (type == 1) {
            tvTitle.setText(R.string.mnemonic_import);
            getSupportFragmentManager().beginTransaction().replace(R.id.ll_content, new LoadWalletByMnemonicFragment()).commit();
        } else if (type == 2) {
            getSupportFragmentManager().beginTransaction().replace(R.id.ll_content, new LoadWalletByPrivateKeyFragment()).commit();
            tvTitle.setText(R.string.private_key_import);
        } else if (type == 3) {
            getSupportFragmentManager().beginTransaction().replace(R.id.ll_content, new LoadWalletByKeyStoreFragment()).commit();
            tvTitle.setText(getString(R.string.keystore_import));
        }
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .navigationBarColor(R.color.black)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 1f)
                .init();
        indicatorView.setSplitAuto(true);
        indicatorView.setOnTransitionListener(new OnTransitionTextListener()
                .setColor(getResources().getColor(R.color.etc_transfer_advanced_setting_help_text_color), getResources().getColor(R.color.discovery_application_item_name_color))
                .setSize(14, 14));
        indicatorView.setScrollBar(new TextWidthColorBar(this, indicatorView, getResources().getColor(R.color.etc_transfer_advanced_setting_help_text_color), UUi.dip2px(2)));
        indicatorView.setScrollBarSize(50);
        indicatorViewPager = new IndicatorViewPager(indicatorView, vpLoadWallet);
        loadWalletPageFragmentAdapter = new LoadWalletPageFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
        indicatorViewPager.setAdapter(loadWalletPageFragmentAdapter);
        indicatorViewPager.setCurrentItem(0, false);
        vpLoadWallet.setOffscreenPageLimit(4);
        rlBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                new RxPermissions(LoadWalletActivity.this)
                        .requestEach(Manifest.permission.CAMERA)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                if (permission.granted) {
                                    Intent intent = new Intent(LoadWalletActivity.this, QRCodeScannerActivity.class);
                                    startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    new AlertDialog.Builder(LoadWalletActivity.this)
                                            .setTitle(R.string.string_apply)
                                            .setMessage(R.string.apply_camera_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                } else {
                                    new AlertDialog.Builder(LoadWalletActivity.this)
                                            .setTitle(R.string.string_apply)
                                            .setMessage(R.string.apply_camera_access)
                                            .setPositiveButton(R.string.to_setting, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //引导用户至设置页手动授权
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                }
                                            })
                                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                        }
                                    }).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        finish();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                EventBus.getDefault().post(new ScanResultEvent(scanResult));
            }
        }
    }

}
