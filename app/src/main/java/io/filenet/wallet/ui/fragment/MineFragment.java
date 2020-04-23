package io.filenet.wallet.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseFragment;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.ui.activity.AboutActivity;
import io.filenet.wallet.ui.activity.ContactActivity;
import io.filenet.wallet.ui.activity.CurrencyUnitSettingActivity;
import io.filenet.wallet.ui.activity.LanguageSettingActivity;
import io.filenet.wallet.utils.WalletDaoUtils;

public class MineFragment extends BaseFragment {
    @BindView(R.id.wallet_name_tv)
    TextView mine_walletname;


    @BindView(R.id.fl_header)
    FrameLayout mHeaderFl;


    private ETHWallet wallet;
    private boolean isPrepared;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        isPrepared = true;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isPrepared) {
            wallet = WalletDaoUtils.getCurrent();
            mine_walletname.setText(wallet.getName());
        }
    }

    @Override
    public void configViews() {
    }


    @OnClick({R.id.lly_curruncy, R.id.lly_help_center, R.id.lly_about_us, R.id.lly_language})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_curruncy:
                intent = new Intent(getActivity(), CurrencyUnitSettingActivity.class);
                break;
            case R.id.lly_language:
                intent = new Intent(getActivity(), LanguageSettingActivity.class);
                break;
            case R.id.lly_help_center:
                intent = new Intent(getActivity(), ContactActivity.class);
                break;
            case R.id.lly_about_us:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            default:
                break;
        }
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(String data) {
    }
}
