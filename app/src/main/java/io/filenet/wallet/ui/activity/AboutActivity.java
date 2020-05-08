package io.filenet.wallet.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.filenet.wallet.BuildConfig;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.entity.UpdateBean;
import io.filenet.wallet.view.AlertPopupWindow;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class AboutActivity extends BaseActivity {

    @BindView(R.id.lly_back)
    LinearLayout lly_back;
    @BindView(R.id.updateRefresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView tvBack;


    private String url = BuildConfig.APIHOST + "/queryVersion?deviceid=";
    private String result = null;
    String szImei = null;

    UpdateBean updatedata;
    private Handler handler = new Handler();

    @Override
    public int getLayoutId() {
        return R.layout.layout_setting_about;
    }

    @Override
    public void initToolBar() {
        tvBack.setImageResource(R.mipmap.app_back_black);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.black));
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        ImmersionBar.with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true, 1f)
                .init();
        tvTitle.setText(R.string.aboutwallet);
    }

    @Override
    public void initDatas() {
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        String verName = "";
        try {
            verName = getApplicationContext().getPackageManager().
                    getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        tvVersion.setText(getResources().getText(R.string.version) + verName);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        szImei = TelephonyMgr.getDeviceId();

    }

    public void getVersion() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().get().url(url + szImei).build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        result = response.body().string();
                        updatedata = new Gson().fromJson(result, UpdateBean.class);
                        queryVersion();
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

    @Override
    public void configViews() {
        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getVersion();
                smartRefreshLayout.finishRefresh(3000);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AboutActivity.this.finish();
        return super.onKeyDown(keyCode, event);
    }

    public void queryVersion() {
        String verName = "";
        try {
            verName = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (updatedata != null && verName != null && updatedata.getData().getVersioncode() != null) {
            String newVersion = updatedata.getData().getVersioncode();
            String minVersion = updatedata.getData().getMinversion();
            if (minVersion != null && verName.compareToIgnoreCase(minVersion) < 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertPopupWindow mAlertDialog = new AlertPopupWindow(mContext);
                        mAlertDialog.setFocusable(false);
                        mAlertDialog.setOutsideTouchable(false);
                        mAlertDialog.setDialogContent(getString(R.string.mustUpdate));
                        mAlertDialog.disableCancleButton();
                        mAlertDialog.setOnInputDialogButtonClickListener(new AlertPopupWindow.OnInputDialogButtonClickListener() {
                            @Override
                            public void onCancel() {
                                mAlertDialog.dismiss();
                            }

                            @Override
                            public void onConfirm() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(String.valueOf(updatedata.getData().getDownloadUrl()));
                                intent.setData(content_url);
                                startActivity(intent);
                                mAlertDialog.dismiss();
                                finish();
                            }
                        });
                        mAlertDialog.show();

                    }
                });
            } else if (verName.compareToIgnoreCase(newVersion) < 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertPopupWindow mAlertDialog = new AlertPopupWindow(mContext);
                        mAlertDialog.setDialogContent(getString(R.string.updateNote));
                        mAlertDialog.enableCancleButton();
                        mAlertDialog.setOnInputDialogButtonClickListener(new AlertPopupWindow.OnInputDialogButtonClickListener() {
                            @Override
                            public void onCancel() {
                                mAlertDialog.dismiss();
                                finish();
                            }

                            @Override
                            public void onConfirm() {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse(String.valueOf(updatedata.getData().getDownloadUrl()));
                                intent.setData(content_url);
                                startActivity(intent);
                                mAlertDialog.dismiss();

                            }
                        });
                        mAlertDialog.show();
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }
}
