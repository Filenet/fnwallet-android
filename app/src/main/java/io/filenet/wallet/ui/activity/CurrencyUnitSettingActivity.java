package io.filenet.wallet.ui.activity;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;

import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.event.UpdatePropertyEvent;
import io.filenet.wallet.utils.SharedPreferencesUtil;

import butterknife.BindView;
import butterknife.OnClick;


public class CurrencyUnitSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.iv_cny)
    Switch ivCNY;
    @BindView(R.id.iv_usd)
    Switch ivUSD;
    private int currencyUnit = 0;

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency_unit_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_currency);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .navigationBarColor(R.color.black)
                .init();
        tvBtn.setVisibility(View.GONE);
    }

    @Override
    public void initDatas() {
        currencyUnit = SharedPreferencesUtil.getInstance().getInt("currencyUnit", 0);
        if (currencyUnit != 0) {
            ivCNY.setChecked(true);
            ivUSD.setChecked(false);
        } else {
            ivCNY.setChecked(false);
            ivUSD.setChecked(true);
        }
    }

    @Override
    public void configViews() {
        ivCNY.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ivUSD.setChecked(false);
                    currencyUnit = 1;
                } else {
                    ivUSD.setChecked(true);
                    currencyUnit = 0;

                }
                SharedPreferencesUtil.getInstance().putInt("currencyUnit", currencyUnit);
                EventBus.getDefault().post(new UpdatePropertyEvent());
            }
        });
        ivUSD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    currencyUnit = 0;
                    ivCNY.setChecked(false);
                } else {
                    currencyUnit = 1;
                    ivCNY.setChecked(true);
                }
                SharedPreferencesUtil.getInstance().putInt("currencyUnit", currencyUnit);
                EventBus.getDefault().post(new UpdatePropertyEvent());
            }
        });

    }

    @OnClick({R.id.rl_cny, R.id.rl_usd, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                SharedPreferencesUtil.getInstance().putInt("currencyUnit", currencyUnit);
                EventBus.getDefault().post(new UpdatePropertyEvent());
                finish();
                break;
            default:
                break;
        }
    }


}
