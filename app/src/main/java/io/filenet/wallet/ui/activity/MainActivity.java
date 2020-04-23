package io.filenet.wallet.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.filenet.wallet.BuildConfig;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.entity.UpdateBean;
import io.filenet.wallet.event.LoadWalletSuccessEvent;
import io.filenet.wallet.ui.adapter.HomePagerAdapter;
import io.filenet.wallet.ui.fragment.MineFragment;
import io.filenet.wallet.ui.fragment.NewPropertyFragment;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.filenet.wallet.view.AlertPopupWindow;
import io.filenet.wallet.view.NoScrollViewPager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.vp_home)
    NoScrollViewPager vpHome;
    @BindView(R.id.iv_mall)
    ImageView ivMall;
    @BindView(R.id.tv_mall)
    TextView tvMall;
    @BindView(R.id.lly_mall)
    LinearLayout llyMall;
    @BindView(R.id.iv_news)
    ImageView ivNews;
    @BindView(R.id.tv_news)
    TextView tvNews;
    @BindView(R.id.lly_news)
    LinearLayout llyNews;
    @BindView(R.id.iv_mine)
    ImageView ivMine;
    @BindView(R.id.tv_mine)
    TextView tvMine;
    @BindView(R.id.lly_mine)
    LinearLayout llyMine;

    private HomePagerAdapter homePagerAdapter;
    private boolean mFirstInit = true;
    private boolean mFormLanguageSettingActivity = false;
    private final String FROMLANGUAGESETTING = "from the LanguageSettingActivity";

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        setImmersionBar(R.color.white, R.color.black, true);

        ivMall.setSelected(true);
        tvMall.setSelected(true);

        llyNews.setOnClickListener(this);
        llyMall.setOnClickListener(this);
        llyMine.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(10);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new NewPropertyFragment());
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);
        vpHome.setOffscreenPageLimit(3);
        vpHome.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0)
                    setImmersionBar(R.color.white, R.color.black, true);
                else
                    setImmersionBar(R.color.colorAccent, R.color.black, false);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        for (ETHWallet ethWallet : WalletDaoUtils.ethWalletDao.loadAll()) {
            LogUtils.e(ethWallet.toString());
        }

    }

    private void setImmersionBar(int statusColor, int navigationColor, boolean isDarkFont){
        ImmersionBar.with(MainActivity.this)
                .statusBarColor(statusColor)
                .statusBarDarkFont(isDarkFont, 1f)
                .navigationBarColor(navigationColor)
                .init();
    }

    private long currentBackPressedTime = 0;
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(mContext, R.string.exit_tips);
                return true;
            } else
                finish();
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall:
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                break;
            case R.id.lly_mine:
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(1, false);
                break;
            default:
                break;
        }
    }

    private void setAllUnselected() {
        ivNews.setSelected(false);
        tvNews.setSelected(false);
        ivMall.setSelected(false);
        tvMall.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        if(!(intent.getStringExtra(FROMLANGUAGESETTING) == null))
            if(intent.getStringExtra(FROMLANGUAGESETTING).equals("yes"))
                mFormLanguageSettingActivity = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirstInit) {
            if(!mFormLanguageSettingActivity) {
                mFirstInit = false;
                versionDetect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ETHWallet wallet) {
        vpHome.setCurrentItem(0, false);
        setAllUnselected();
        ivMall.setSelected(true);
        tvMall.setSelected(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(LoadWalletSuccessEvent loadWalletSuccessEvent) {
        if (WalletDaoUtils.getCurrent() != null) {
            vpHome.setCurrentItem(0, false);
            setAllUnselected();
            ivMall.setSelected(true);
            tvMall.setSelected(true);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, FirstGuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }


    private String url = BuildConfig.APIHOST + "/queryVersion?deviceid=";
    private String result = null;
    String szImei = null;

    private void versionDetect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().get().url(url + szImei).build();
                Call call = okHttpClient.newCall(request);

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException, IllegalStateException {
                        result = response.body().string();
                        try {
                            UpdateBean updatedata = new Gson().fromJson(result, UpdateBean.class);
                            queryVersion(updatedata);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Handler handler = new Handler();

    public void queryVersion(UpdateBean updatedata) {
        try {
            String verName = this.getPackageManager().
                    getPackageInfo(this.getPackageName(), 0).versionName;
            if (updatedata != null && !TextUtils.isEmpty(verName) && !TextUtils.isEmpty(updatedata.getData().getVersioncode())) {
                String newVersion = updatedata.getData().getVersioncode();
                String minVersion = updatedata.getData().getMinversion();
                if (minVersion != null && verName.compareToIgnoreCase(minVersion) < 0){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showAlertPopupWindow(updatedata, getString(R.string.mustUpdate), false);
                        }
                    });
                } else if (verName.compareToIgnoreCase(newVersion) < 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showAlertPopupWindow(updatedata, getString(R.string.updateNote), true);
                        }
                    });
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void showAlertPopupWindow(UpdateBean updatedata, String dialogContent, boolean showCancleButton){
        AlertPopupWindow mAlertDialog = new AlertPopupWindow(mContext);
        mAlertDialog.setDialogContent(dialogContent);
        if (showCancleButton) mAlertDialog.enableCancleButton();
        else {
            mAlertDialog.setFocusable(false);
            mAlertDialog.setOutsideTouchable(false);
            mAlertDialog.disableCancleButton();
        }
        mAlertDialog.setOnInputDialogButtonClickListener(new AlertPopupWindow.OnInputDialogButtonClickListener() {
            @Override
            public void onCancel() {
                if (showCancleButton) {
                    mAlertDialog.dismiss();
                }
            }

            @Override
            public void onConfirm() {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(String.valueOf(updatedata.getData().getDownloadUrl()));
                intent.setData(content_url);
                startActivity(intent);
                mAlertDialog.dismiss();
                if (!showCancleButton) finish();
            }
        });
        mAlertDialog.show();
    }
}
