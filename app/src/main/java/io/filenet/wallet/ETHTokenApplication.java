package io.filenet.wallet;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.LocaleList;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.scwang.smartrefresh.header.WaterDropHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.util.Locale;

import io.filenet.wallet.domain.DaoMaster;
import io.filenet.wallet.domain.DaoSession;
import io.filenet.wallet.utils.AppFilePath;
import io.filenet.wallet.utils.AppUtils;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.SharedPreferencesUtil;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;


public class ETHTokenApplication extends MultiDexApplication {

    private static ETHTokenApplication sInstance;
    private boolean isFirstInstall = true;
    public static final String ARG_FIRST_INSTALL = "ARG_FIRST_INSTALL";

    private DaoSession daoSession;


    public DaoSession getDaoSession() {
        return daoSession;
    }


    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);
                return new WaterDropHeader(context);

            }
        });
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageUtil.setLocal(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtil.setLocal(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "98ecf28097", false);
        sInstance = this;
        AppUtils.init(this);
        AppFilePath.init(this);

        UMConfigure.init(this, "5c3fe3a1b465f5eb10000a0f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);

        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        initPrefs();
        initGreenDao();
        SharedPreferencesUtil.getInstance().getBoolean("ARG_FIRST_INSTALL", false);

        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        });
    }

    private void initGreenDao() {
        GawOpenHelper mHelper = new GawOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public static ETHTokenApplication getsInstance() {
        return sInstance;
    }

    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    private void initLanguage() {
        String language = SharedPreferencesUtil.getInstance().getString("language");
        if (TextUtils.isEmpty(language)) {
            language = Locale.getDefault().getLanguage();
            SharedPreferencesUtil.getInstance().putString("language", language);
        } else {
            if (language.equals(Locale.CHINESE.toString())) {
                setLanguage(Locale.CHINESE);
            } else {
                setLanguage(Locale.ENGLISH);
            }
        }
    }

    private void setLanguage(Locale chinese) {

        Resources resources = getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.setLocale(chinese);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(chinese);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            getApplicationContext().createConfigurationContext(config);
        }
        Locale.setDefault(chinese);
        resources.updateConfiguration(config, dm);

    }


}
