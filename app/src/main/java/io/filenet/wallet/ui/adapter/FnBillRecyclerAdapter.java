package io.filenet.wallet.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.entity.FnTrBills;
import io.filenet.wallet.utils.WalletDaoUtils;


public class FnBillRecyclerAdapter extends RecyclerView.Adapter<FnBillRecyclerAdapter.ViewHolder> implements View.OnClickListener {


    public List<FnTrBills.DataBean.PageBean.RecordsBean> mData;

    public LayoutInflater mInflater;

    FnTrBills.DataBean.PageBean.RecordsBean recordsBean;

    private String fnaddress;

    public void setFnaddress(String add) {
        this.fnaddress = add;
    }

    public FnBillRecyclerAdapter(List<FnTrBills.DataBean.PageBean.RecordsBean> data) {
        mData = data;
    }

    private FnTrBills.DataBean.PageBean.RecordsBean getData(int position) {
        return mData.get(position);
    }

    @NonNull
    @Override
    public FnBillRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
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

    private FnBillRecyclerAdapter.OnItemClickListener mItemClickListener;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        recordsBean = getData(i);
        ETHWallet wallet = WalletDaoUtils.getCurrent();
        String walletAddress = wallet.getAddress();
        if (recordsBean.getFrom().equals(fnaddress)) {
            viewHolder.fnInout.setImageResource(R.mipmap.bills_out);
            viewHolder.addressFrom.setText(recordsBean.getTo());
            viewHolder.fnValue.setTextColor(ContextCompat.getColor(viewHolder.fnValue.getContext(), R.color.color_FF6677));
            viewHolder.fnValue.setText(String.format("-%.4f", new BigDecimal(recordsBean.getValue()).divide(new BigDecimal("1000000000"))) + "FN");
        } else {
            viewHolder.addressFrom.setText(recordsBean.getFrom());
            viewHolder.fnInout.setImageResource(R.mipmap.bills_in);
            viewHolder.fnValue.setTextColor(ContextCompat.getColor(viewHolder.fnValue.getContext(), R.color.color_7ED321));
            viewHolder.fnValue.setText(String.format("+%.4f", new BigDecimal(recordsBean.getValue()).divide(new BigDecimal("1000000000"))) + "FN");
        }
        viewHolder.recordTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(recordsBean.getTimestamp() - 8 * 3600 + "000"))) + "(UTC)");
        viewHolder.itemView.setTag(i);
    }

    public List<FnTrBills.DataBean.PageBean.RecordsBean> getDatas() {
        return mData;
    }

    public void clearData() {
        mData.clear();
        notifyItemRangeRemoved(0, mData.size());
    }

    public void setData(List<FnTrBills.DataBean.PageBean.RecordsBean> datas) {
        mData = datas;
        notifyDataSetChanged();
    }


    public void addData(List<FnTrBills.DataBean.PageBean.RecordsBean> datas) {
        if (mData == null) {
            mData = new ArrayList<FnTrBills.DataBean.PageBean.RecordsBean>();
        }
        mData.addAll(datas);
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
