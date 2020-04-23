package io.filenet.wallet.ui.adapter;

import android.content.Context;
import android.graphics.Color;

import java.util.List;

import io.filenet.wallet.R;
import io.filenet.wallet.base.CommonAdapter;
import io.filenet.wallet.base.ViewHolder;
import io.filenet.wallet.domain.ETHWallet;


public class DrawerWalletAdapter extends CommonAdapter<ETHWallet> {

    private int currentWalletPosition = 0;

    public DrawerWalletAdapter(Context context, List<ETHWallet> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public void setCurrentWalletPosition(int currentWalletPosition) {
        this.currentWalletPosition = currentWalletPosition;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, ETHWallet wallet) {
        boolean isCurrent = wallet.getIsCurrent();
        int position = holder.getPosition();
        if (isCurrent) {
            currentWalletPosition = position;
            holder.getView(R.id.lly_wallet).setBackgroundColor(mContext.getResources().getColor(R.color.color_f8f8fa));
        } else {
            holder.getView(R.id.lly_wallet).setBackgroundColor(Color.WHITE);
        }


        holder.setText(R.id.tv_wallet_name, wallet.getName());
    }
}
