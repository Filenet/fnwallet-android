package io.filenet.wallet.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.filenet.wallet.R;
import io.filenet.wallet.entity.MinEarnBean;


public class FnBillMineAdapter extends RecyclerView.Adapter<FnBillMineAdapter.ViewHolder> implements View.OnClickListener {


    public List<MinEarnBean.DataBean.BlocksBean> mData;

    public LayoutInflater mInflater;

    MinEarnBean.DataBean.BlocksBean recordsBean;

    private String fnaddress;

    public void setFnaddress(String add) {
        this.fnaddress = add;
    }

    public FnBillMineAdapter(List<MinEarnBean.DataBean.BlocksBean> data) {
        mData = data;
    }


    @NonNull
    @Override
    public FnBillMineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mInflater = LayoutInflater.from(viewGroup.getContext());
        View view = mInflater.inflate(R.layout.fn_bills_item, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }


    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick((Integer) view.getTag());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private FnBillMineAdapter.OnItemClickListener mItemClickListener;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        recordsBean = mData.get(i);
        if (recordsBean != null) {
            viewHolder.fnValue.setText("+" + new BigDecimal(recordsBean.getReward()).divide(new BigDecimal("1000000000")).toString() + "FN");
            viewHolder.addressFrom.setText(recordsBean.getHash());
            viewHolder.recordTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(recordsBean.getTimestamp() - 8 * 3600 + "000"))) + "(UTC)");
            viewHolder.itemView.setTag(i);
        }
    }

    public List<MinEarnBean.DataBean.BlocksBean> getDatas() {
        return mData;
    }

    public void clearData() {
        mData.clear();
        notifyItemRangeRemoved(0, mData.size());
    }

    public void setData(List<MinEarnBean.DataBean.BlocksBean> datas) {
        mData = datas;
        notifyDataSetChanged();
    }


    public void addData(List<MinEarnBean.DataBean.BlocksBean> datas) {
        if (mData == null) {
            mData = new ArrayList<MinEarnBean.DataBean.BlocksBean>();
        }
        if (datas != null) {
            mData.addAll(datas);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mData != null && mData.size() > 0) {
            return mData.size();
        }
        return 0;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView addressFrom;
        TextView recordTime;
        TextView fnValue;
        ImageView fnInout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            addressFrom = itemView.findViewById(R.id.fn_tr_bills_address);
            recordTime = itemView.findViewById(R.id.fn_tr_bills_time);
            fnValue = itemView.findViewById(R.id.fn_tr_amount);
            fnInout = itemView.findViewById(R.id.fn_inout);
        }
    }

}
