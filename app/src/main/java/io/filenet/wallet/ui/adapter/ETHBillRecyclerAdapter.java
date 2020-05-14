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
import java.util.Date;
import java.util.List;

import io.filenet.wallet.R;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.entity.ETHBills;
import io.filenet.wallet.utils.WalletDaoUtils;


public class ETHBillRecyclerAdapter extends RecyclerView.Adapter<ETHBillRecyclerAdapter.ViewHolder> implements View.OnClickListener {


    public List<ETHBills.ResultBean> mData;

    public LayoutInflater mInflater;

    public ETHBills.ResultBean test;

    private String address;

    public void setAdd(String add) {
        address = add;
    }

    public ETHBillRecyclerAdapter(List<ETHBills.ResultBean> data) {
        mData = data;
    }

    private ETHBills.ResultBean getData(int position) {
        return mData.get(position);
    }

    @NonNull
    @Override
    public ETHBillRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mInflater = LayoutInflater.from(viewGroup.getContext());
        View view = mInflater.inflate(R.layout.ethbills_item, viewGroup, false);
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

    private OnItemClickListener mItemClickListener;

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        test = getData(i);
        ETHWallet wallet = WalletDaoUtils.getCurrent();
        String walletAddress = wallet.getAddress();
        if (address.equals(test.getFrom())) {
            viewHolder.ethAddFrom.setText(test.getTo());
            viewHolder.ethInOut.setImageResource(R.mipmap.bills_out);
            viewHolder.ethValue.setTextColor(ContextCompat.getColor(viewHolder.ethValue.getContext(), R.color.color_FF6677));
            viewHolder.ethValue.setText(String.format("-%.4f", new BigDecimal(test.getValue()).divide(new BigDecimal("1000000000000000000"))) + " ETH");

        } else {
            viewHolder.ethAddFrom.setText(test.getFrom());
            viewHolder.ethInOut.setImageResource(R.mipmap.bills_in);
            viewHolder.ethValue.setTextColor(ContextCompat.getColor(viewHolder.ethValue.getContext(), R.color.color_7ED321));
            viewHolder.ethValue.setText(String.format("+%.4f", new BigDecimal(test.getValue()).divide(new BigDecimal("1000000000000000000"))) + " ETH");

        }

        viewHolder.ethDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(test.getTimeStamp() + "000"))));
        viewHolder.itemView.setTag(i);
    }

    public List<ETHBills.ResultBean> getDatas() {
        return mData;
    }

    public void clearData() {

        mData.clear();
        notifyItemRangeRemoved(0, mData.size());
    }

    public void addData(List<ETHBills.ResultBean> datas) {
        addData(0, datas);
    }

    public void setData(List<ETHBills.ResultBean> datas) {
        this.mData = datas;
        notifyDataSetChanged();
    }

    public void addData(int position, List<ETHBills.ResultBean> datas) {
        if (datas != null && datas.size() > 0) {
            mData.addAll(datas);
            notifyDataSetChanged();
        }
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
        ImageView ethInOut;
        TextView ethAddFrom;
        TextView ethDate;
        TextView ethValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ethInOut = itemView.findViewById(R.id.iv_inorout);
            ethAddFrom = itemView.findViewById(R.id.tv_bills_address);
            ethDate = itemView.findViewById(R.id.tv_bills_time);
            ethValue = itemView.findViewById(R.id.eth_amount);
        }
    }
}
