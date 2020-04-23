package io.filenet.wallet.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import io.filenet.wallet.BuildConfig;
import io.filenet.wallet.R;
import io.filenet.wallet.entity.UpdateBean;
import io.filenet.wallet.ui.adapter.ViewPagerAdapter;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.SPLanguage;
import io.filenet.wallet.utils.SharedPreferencesUtil;
import io.filenet.wallet.view.AlertPopupWindow;
import io.filenet.wallet.view.CircleIndicator;
import io.filenet.wallet.view.ImportWalletWindow;
import io.filenet.wallet.view.NoScrollViewPager;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FirstGuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {


    boolean isFirstUse;
    private Context mContext;
    private NoScrollViewPager viewPager;

    private CircleIndicator circleIndicator;
    private ViewPagerAdapter vpAdapter;

    private ArrayList<View> views;


    private View view1, view2, view3, view4;


    private TextView startBt, langBt;


    private Button btnImport, btnCreate;

    Disposable subscribe;
    private static int REQUEST_EXTERNAL_STRONGE = 1;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.setLocal(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_first_guide);
        initView();
        initData();
    }

    private void initView() {
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.guide_ui01, null);
        view2 = mLi.inflate(R.layout.guide_ui02, null);
        view3 = mLi.inflate(R.layout.guide_ui03, null);
        view4 = mLi.inflate(R.layout.activity_guide_create_wallet, null);
        viewPager = (NoScrollViewPager) findViewById(R.id.viewpager);
        circleIndicator = findViewById(R.id.indicator);
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        viewPager.setScroll(true);
        vpAdapter = new ViewPagerAdapter(views);

        startBt = (TextView) view3.findViewById(R.id.startBtn);

        langBt = (TextView) view4.findViewById(R.id.tv_start_lang);


        btnImport = view4.findViewById(R.id.guid_import);
        btnCreate = view4.findViewById(R.id.guid_creat);
    }


    private void initData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STRONGE);
        }
        isFirstUse = SharedPreferencesUtil.getInstance().getBoolean("isFirstUse", true);
        SharedPreferencesUtil.getInstance().putBoolean("isFirstUse", false);
        viewPager.addOnPageChangeListener(this);


        viewPager.setAdapter(vpAdapter);
        circleIndicator.setViewPager(viewPager);


        langBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Locale locale = LanguageUtil.getSetLanguageLocale(getApplicationContext());
                int locSel;
                if (Locale.ENGLISH.equals(locale)){
                    locSel = 1;
                }else{
                    locSel = 2;
                }
                SPLanguage.getInstance(getApplicationContext()).saveLanguage(locSel);
                LanguageUtil.setLocal(getApplicationContext());
                recreate();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(FirstGuideActivity.this, CreateWalletActivity.class);
                startActivity(intent);
            }
        });

        btnImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImportWalletWindow importWalletWindow = new ImportWalletWindow(mContext);
                importWalletWindow.setOnInputDialogButtonClickListener(new ImportWalletWindow.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        importWalletWindow.dismiss();
                    }

                    @Override
                    public void onMnemonic() {
                        importWalletWindow.dismiss();
                        Intent intent = new Intent();
                        intent.setClass(FirstGuideActivity.this, LoadWalletActivity.class);
                        intent.putExtra("import_type", 1);
                        startActivity(intent);
                    }

                    @Override
                    public void onPrivatekey() {
                        importWalletWindow.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("import_type", 2);
                        intent.setClass(FirstGuideActivity.this, LoadWalletActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onKeyStore() {
                        importWalletWindow.dismiss();
                        Intent intent = new Intent();
                        intent.putExtra("import_type", 3);
                        intent.setClass(FirstGuideActivity.this, LoadWalletActivity.class);
                        startActivity(intent);
                    }
                });
                importWalletWindow.show();
            }
        });
        ImmersionBar.with(this).statusBarDarkFont(true, 1f).init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        versionDetect();
        if (!isFirstUse) {
            viewPager.setCurrentItem(3);
            viewPager.setScroll(false);
            circleIndicator.setVisibility(View.GONE);
            ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).statusBarDarkFont(false, 1f).init();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        switch (arg0) {
            case 3:
                ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).statusBarDarkFont(false, 1f).init();
                circleIndicator.setVisibility(View.GONE);
                viewPager.setScroll(false);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            subscribe.dispose();
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


