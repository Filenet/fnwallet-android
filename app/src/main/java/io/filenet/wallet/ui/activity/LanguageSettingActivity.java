package io.filenet.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.utils.LanguageUtil;
import io.filenet.wallet.utils.SPLanguage;


public class LanguageSettingActivity extends BaseActivity {
    private static final String IS_CHANGED = "isChanged";
    private static final String FROMTHELANGUAGESETTINGACTIVITY = "from the LanguageSettingActivity";
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.iv_chinese)
    Switch ivChinese;
    @BindView(R.id.iv_English)
    Switch ivEnglish;

    @BindView(R.id.lly_back)
    LinearLayout lly_back;
    private int language;

    @Override
    public int getLayoutId() {
        return R.layout.activity_language_setting;
    }

    private boolean isChanged = false;

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_language);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
        tvBtn.setVisibility(View.GONE);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .navigationBarColor(R.color.black)
                .init();
    }

    @Override
    public void initDatas() {
        language = SPLanguage.getInstance(this).getSelectLanguage();
        if (language == 0) {
            language = LanguageUtil.getSystemLocale(this).toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) ? 1 : 2;
        }
        if (language == 1) {
            ivChinese.setChecked(true);
            ivEnglish.setChecked(false);
        } else if (language == 2) {
            ivChinese.setChecked(false);
            ivEnglish.setChecked(true);
        }
    }

    @Override
    public void configViews() {
        lly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChanged) {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(FROMTHELANGUAGESETTINGACTIVITY, "yes");
                    startActivity(intent);
                } else {
                    onBackPressed();
                }
            }
        });
        ivChinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    language = 1;
                    ivEnglish.setChecked(false);
                } else {
                    language = 2;
                    ivEnglish.setChecked(true);

                }
                SPLanguage.getInstance(mContext).saveLanguage(language);
                LanguageUtil.setLocal(getApplicationContext());
                isChanged = true;
                recreate();
            }
        });
        ivEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ivChinese.setChecked(false);
                    language = 2;
                } else {
                    ivChinese.setChecked(true);
                    language = 1;
                }
                SPLanguage.getInstance(mContext).saveLanguage(language);
                LanguageUtil.setLocal(getApplicationContext());
                isChanged = true;
                recreate();
            }
        });


    }

    @OnClick({R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                SPLanguage.getInstance(this).saveLanguage(language);
                LanguageUtil.setLocal(getApplicationContext());
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(FROMTHELANGUAGESETTINGACTIVITY, "yes");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_CHANGED, isChanged);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isChanged = savedInstanceState.getBoolean(IS_CHANGED);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isChanged) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("from the LanguageSettingActivity", "yes");
                startActivity(intent);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


}
