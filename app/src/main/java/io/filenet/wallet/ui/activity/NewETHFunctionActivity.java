package io.filenet.wallet.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.filenet.wallet.BuildConfig;
import io.filenet.wallet.R;
import io.filenet.wallet.base.BaseActivity;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.entity.ETHBills;
import io.filenet.wallet.ui.adapter.ETHBillRecyclerAdapter;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewETHFunctionActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.lly_back)
    LinearLayout llyBack;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.tv_eth_total)
    TextView tvEthTotal;
    @BindView(R.id.tv_eth_value)
    TextView tvEthValue;
    @BindView(R.id.ll_receivables)
    LinearLayout llReceivables;
    @BindView(R.id.ll_transfer)
    LinearLayout llTransfer;
    @BindView(R.id.bill_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_asset)
    TextView tvNoAsset;
    @BindView(R.id.swipeRefresh)
    SmartRefreshLayout swipeRefresh;

    private String ethTotal;
    private String ethTotalValue;
    private int page = 1;
    public List<ETHBills.ResultBean> datas = new ArrayList<>();
    public LinearLayoutManager linearLayoutManager;
    public ETHBillRecyclerAdapter recyclerAdapter;

    private Disposable mDisposable;

    ETHBills ethBillsbean = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_eth_function;
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        ivBack.setImageResource(R.mipmap.app_back_white);
        tvTitle.setText(WalletDaoUtils.getCurrent().getName());
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
        llyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImmersionBar.with(this)
                .titleBar(mCommonToolbar, false)
                .transparentStatusBar()
                .statusBarDarkFont(false, 1f)
                .navigationBarColor(R.color.black)
                .init();

    }

    @Override
    public void initDatas() {
        ethTotal = getIntent().getStringExtra("ethTotal");
        ethTotalValue = getIntent().getStringExtra("ethValue");
        tvEthTotal.setText(ethTotal);
        tvEthValue.setText(ethTotalValue);
    }

    @Override
    public void configViews() {
        llReceivables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewETHFunctionActivity.this, GatheringQRCodeActivity.class);
                intent.putExtra("walletAddress", WalletDaoUtils.getCurrent().getAddress());
                startActivity(intent);
            }
        });
        llTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewETHFunctionActivity.this, ETHTransferActivity.class);
                intent.putExtra("walletAddress", WalletDaoUtils.getCurrent().getAddress());
                intent.putExtra("walletPwd", WalletDaoUtils.getCurrent().getPassword());
                startActivity(intent);
            }
        });

        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerAdapter = new ETHBillRecyclerAdapter(datas);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerAdapter.setItemClickListener(new ETHBillRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(NewETHFunctionActivity.this, ETHBillDetailActivity.class);
                intent.putExtra("from", datas.get(position).getFrom());
                intent.putExtra("to", datas.get(position).getTo());
                intent.putExtra("gasprice", datas.get(position).getGasPrice());
                intent.putExtra("hashnum", datas.get(position).getHash());
                intent.putExtra("blocknum", datas.get(position).getBlockNumber());
                intent.putExtra("value", datas.get(position).getValue());
                intent.putExtra("timestamp", datas.get(position).getTimeStamp());
                startActivity(intent);
            }
        });
        swipeRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                loadDate();
            }
        });
        swipeRefresh.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                page++;
                loadDate();
            }
        });
        swipeRefresh.autoRefresh();

    }



    @SuppressLint("CheckResult")
    private void loadDate() {
        ETHWallet wallet = WalletDaoUtils.getCurrent();
        String walletAddress = wallet.getAddress();
        recyclerAdapter.setAdd(walletAddress.toLowerCase());
        mDisposable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                String apikey = "HYAHB34F8C34ZXDIJ1K9WVQ7PG1KIZX1SE";
                String billUrl = BuildConfig.APIHOST + "/ETH?json=http%3A%2F%2Fapi.etherscan.io%2Fapi%3Fmodule%3Daccount%26action%3Dtxlist%26address%3D"
                        + walletAddress + "%26startblock%3D0%26endblock%3D99999999%26" + page + "%3D1%26offset%3D10%26sort%3Dasc%26apikey%3D" + apikey;
                LogUtils.e(billUrl);
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().get().url(billUrl).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    e.onNext(response.body().string());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    e.onError(e1);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                               @Override
                               public void accept(String s) throws Exception {
                                   swipeRefresh.finishRefresh();
                                   swipeRefresh.finishLoadMore();
                                   ethBillsbean = new Gson().fromJson(s, ETHBills.class);
                                   if (ethBillsbean != null && ethBillsbean.getStatus().equals("1")) {
                                       datas = ethBillsbean.getResult();
                                   }
                                   if (page == 1) {
                                       recyclerAdapter.setData(datas);
                                   } else {
                                       recyclerAdapter.addData(datas);
                                   }
                                   if (datas != null && datas.size() < 10) {
                                       swipeRefresh.setNoMoreData(true);
                                   }
                                   if (recyclerAdapter.getDatas() == null || recyclerAdapter.getDatas().size() == 0) {
                                       tvNoAsset.setVisibility(View.VISIBLE);
                                   } else {
                                       tvNoAsset.setVisibility(View.GONE);
                                   }
                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   ToastUtils.showToast(mContext, R.string.network_error);
                                   swipeRefresh.setNoMoreData(true);
                               }
                           }, new Action() {
                               @Override
                               public void run() throws Exception {

                                   swipeRefresh.finishRefresh();
                                   swipeRefresh.finishLoadMore();
                               }
                           }

                );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
