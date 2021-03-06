package io.filenet.wallet.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.filenet.wallet.BuildConfig;
import io.filenet.wallet.R;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.entity.MinEarnBean;
import io.filenet.wallet.ui.activity.FnMineRecordDetailActivity;
import io.filenet.wallet.ui.adapter.FnBillMineAdapter;
import io.filenet.wallet.utils.Base58;
import io.filenet.wallet.utils.LogUtils;
import io.filenet.wallet.utils.ToastUtils;
import io.filenet.wallet.utils.WalletDaoUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FnMineListFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    private SmartRefreshLayout fn_swipeRefresh;
    private RecyclerView fn_bill_recycler_view;
    private TextView tv_no_asset;
    private FnBillMineAdapter fnRecyclerAdapter;
    private int page = 1;
    private Context mContext;
    private String walletAddressBase58;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fntrans, container, false);
        fn_swipeRefresh = view.findViewById(R.id.fn_swipeRefresh);
        fn_bill_recycler_view = view.findViewById(R.id.fn_bill_recycler_view);
        tv_no_asset = view.findViewById(R.id.tv_no_asset);

        fn_swipeRefresh.setOnRefreshListener(this);
        fn_swipeRefresh.setOnLoadMoreListener(this);

        fn_bill_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        fnRecyclerAdapter = new FnBillMineAdapter(new ArrayList<>());
        fn_bill_recycler_view.setAdapter(fnRecyclerAdapter);
        ETHWallet wallet = WalletDaoUtils.getCurrent();
        if (wallet != null) {
            String walletAddress = wallet.getAddress();
            walletAddressBase58 = walletAddress.toLowerCase().substring(2, walletAddress.length());
            fnRecyclerAdapter.setFnaddress(walletAddressBase58);
        }

        fnRecyclerAdapter.setItemClickListener(new FnBillMineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), FnMineRecordDetailActivity.class);
                intent.putExtra("data", fnRecyclerAdapter.getDatas().get(position));
                startActivity(intent);
            }
        });

        fn_swipeRefresh.autoRefresh();
        return view;
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        page = 1;
        loadDate();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        page++;
        loadDate();
    }

    private void loadDate() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                OkHttpClient client = new OkHttpClient();
                ETHWallet wallet = WalletDaoUtils.getCurrent();
                String url = BuildConfig.APIHOST + "/sendMining?addars=" + Base58.encode(walletAddressBase58.getBytes()) + "&num=" + page + "&size=20";
                Request request = new Request.Builder().url(url).get().build();
                Response response = client.newCall(request).execute();
                String responseData = response.body().string();
                e.onNext(responseData);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        LogUtils.e(s);
                        fn_swipeRefresh.finishRefresh();
                        fn_swipeRefresh.finishLoadMore();
                        Gson gson = new Gson();
                        MinEarnBean fnBills = gson.fromJson(s, MinEarnBean.class);
                        if (fnBills != null) {
                            if (page == 1) {
                                fnRecyclerAdapter.setData(fnBills.getData().getRecords());
                            } else {
                                fnRecyclerAdapter.addData(fnBills.getData().getRecords());
                            }
                        }
                        if (fnBills == null || fnBills.getData().getRecords() == null || fnBills.getData().getRecords().size() < 20) {
                            fn_swipeRefresh.setNoMoreData(true);
                        }

                        if (fnRecyclerAdapter.getDatas() == null || fnRecyclerAdapter.getDatas().size() == 0) {
                            tv_no_asset.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_asset.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast(mContext, R.string.network_error);
                        fn_swipeRefresh.finishRefresh();
                        fn_swipeRefresh.finishLoadMore();
                    }

                    @Override
                    public void onComplete() {
                        fn_swipeRefresh.finishRefresh();
                        fn_swipeRefresh.finishLoadMore();
                    }
                });
    }
}