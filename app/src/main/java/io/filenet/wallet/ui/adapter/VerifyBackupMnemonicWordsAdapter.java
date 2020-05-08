package io.filenet.wallet.ui.adapter;

import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.filenet.wallet.R;
import io.filenet.wallet.domain.VerifyMnemonicWordTag;


public class VerifyBackupMnemonicWordsAdapter extends BaseQuickAdapter<VerifyMnemonicWordTag, BaseViewHolder> {

    public VerifyBackupMnemonicWordsAdapter(int layoutResId, @Nullable List<VerifyMnemonicWordTag> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VerifyMnemonicWordTag verifyMnemonicWordTag) {
        LinearLayout llyTag = helper.itemView.findViewById(R.id.lly_tag);
        if (verifyMnemonicWordTag.isSelected()) {
            llyTag.setBackground(mContext.getDrawable(R.drawable.shape_mnemnic_bg));
            helper.setTextColor(R.id.tv_mnemonic_word, mContext.getResources().getColor(R.color.white));
        } else {
            llyTag.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            helper.setTextColor(R.id.tv_mnemonic_word, mContext.getResources().getColor(R.color.color_3B5998));
        }
        helper.setText(R.id.tv_mnemonic_word, verifyMnemonicWordTag.getMnemonicWord());
    }

    public boolean setSelection(int position) {

        VerifyMnemonicWordTag verifyMnemonicWordTag = getData().get(position);
        if (verifyMnemonicWordTag.isSelected()) {
            return false;
        }
        verifyMnemonicWordTag.setSelected(true);
        notifyDataSetChanged();
        return true;
    }

    public boolean setUnselected(int position) {

        VerifyMnemonicWordTag verifyMnemonicWordTag = getData().get(position);
        if (!verifyMnemonicWordTag.isSelected()) {
            return false;
        }
        verifyMnemonicWordTag.setSelected(false);
        notifyDataSetChanged();
        return true;
    }

}
