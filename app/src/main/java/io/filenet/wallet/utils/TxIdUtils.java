package io.filenet.wallet.utils;


import org.bitcoinj.core.Sha256Hash;

import java.nio.ByteBuffer;

import io.filenet.wallet.entity.Proxy;
import io.filenet.wallet.entity.Receipt;
import io.filenet.wallet.entity.Transfer;
import io.filenet.wallet.entity.VoteContent;

public class TxIdUtils {

    public static String CreateTxIdTran(Transfer t) {

        byte[] headers = FnWalletUtils.dataAppend(t.getFrom(), t.getTocount(), t.getValue(), t.getTimestamp());
        System.out.println(headers);
        for (int i = 0; i < t.getTocount(); i++) {
            headers = FnWalletUtils.dataAppend(
                    headers, //getTransferDetails
                    t.getTransferdetails().get(i).getTo(),
                    t.getTransferdetails().get(i).getValue()
            );
        }

        byte[] hash = Sha256Hash.hash(headers);
        return ByteUtils.bytesToHexString(hash);

    }

    public static byte[] CreateTxIdTranTe(Transfer t) {

        byte[] headers = FnWalletUtils.dataAppend(t.getFrom(), t.getTocount(), t.getValue(), t.getTimestamp());
        for (int i = 0; i < t.getTocount(); i++) {
            headers = FnWalletUtils.dataAppend(
                    headers, //getTransferDetails
                    t.getTransferdetails().get(i).getTo(),
                    t.getTransferdetails().get(i).getValue()
            );
        }
        System.out.println(ByteUtils.bytesToHexString(headers));
        return headers;


    }

    public static String CreateTxIdReceipt(Receipt r) {

        byte[] headers = FnWalletUtils.dataAppendReceipt(r.getTxid(), r.getfrom(), r.getAccept(), r.getTimestamp());
        byte[] hash = Sha256Hash.hash(headers);
        return ByteUtils.bytesToHexString(hash);
    }


    public static String CreateTxIdTranProxy(Proxy proxy) {
        byte[] from = ByteUtils.hexStringToByte(proxy.getFrom());
        byte[] to = ByteUtils.hexStringToByte(proxy.getTo());
        byte[] value = ByteUtils.getBytes(proxy.getValue());
        byte[] time = ByteUtils.getBytes(proxy.getTimestamp());
        byte[] expired = ByteUtils.getBytes(proxy.getExpired());
        ByteBuffer buffer = ByteBuffer.allocate(from.length + to.length + value.length + time.length + expired.length);
        byte[] var10000 = buffer.put(from).put(to).put(value).put(time).put(expired).array();
        byte[] hash = Sha256Hash.hash(var10000);
        return ByteUtils.bytesToHexString(hash);
    }

    public static byte[] CreateTxIdTranProxyTe(Proxy proxy) {
        byte[] from = ByteUtils.hexStringToByte(proxy.getFrom());
        byte[] to = ByteUtils.hexStringToByte(proxy.getTo());
        byte[] value = ByteUtils.getBytes(proxy.getValue());
        byte[] time = ByteUtils.getBytes(proxy.getTimestamp());
        byte[] expired = ByteUtils.getBytes(proxy.getExpired());
        ByteBuffer buffer = ByteBuffer.allocate(from.length + to.length + value.length + time.length + expired.length);
        byte[] var10000 = buffer.put(from).put(to).put(value).put(time).put(expired).array();
        return var10000;
    }


    public static String CreateTxIdTranVoteContent(VoteContent voteContent) {
        byte[] from = ByteUtils.hexStringToByte(voteContent.getFrom());
        byte[] to = ByteUtils.hexStringToByte(voteContent.getTo());
        byte[] value = ByteUtils.getBytes(voteContent.getValue());
        byte[] time = ByteUtils.getBytes(voteContent.getTimestamp());
        byte[] expired = ByteUtils.getBytes(voteContent.getExpired());
        ByteBuffer buffer = ByteBuffer.allocate(from.length + to.length + value.length + time.length + expired.length);
        byte[] var10000 = buffer.put(from).put(to).put(value).put(time).put(expired).array();
        byte[] hash = Sha256Hash.hash(var10000);
        return ByteUtils.bytesToHexString(hash);
    }

    public static byte[] CreateTxIdTranVoteContentTe(VoteContent voteContent) {
        byte[] from = ByteUtils.hexStringToByte(voteContent.getFrom());
        byte[] to = ByteUtils.hexStringToByte(voteContent.getTo());
        byte[] value = ByteUtils.getBytes(voteContent.getValue());
        byte[] time = ByteUtils.getBytes(voteContent.getTimestamp());
        byte[] expired = ByteUtils.getBytes(voteContent.getExpired());
        ByteBuffer buffer = ByteBuffer.allocate(from.length + to.length + value.length + time.length + expired.length);
        byte[] var10000 = buffer.put(from).put(to).put(value).put(time).put(expired).array();
        return var10000;
    }

    public static byte[] CreateTxIdReceiptTe(Receipt receipt) {
        byte[] txid = ByteUtils.hexStringToByte(receipt.getTxid());
        byte[] from = ByteUtils.hexStringToByte(receipt.getFrom());
        byte bool = 0;
        if (receipt.getAccept() == 1) {
            bool = 1;
        } else {
            bool = 0;
        }
        byte[] time = ByteUtils.getBytes(receipt.getTimestamp());
        ByteBuffer buffer = ByteBuffer.allocate(txid.length + from.length + 1 + time.length);
        byte[] var10000 = buffer.put(txid).put(from).put(bool).put(time).array();
        return var10000;
    }


    public static String CreateTxIdTranReceipt(Receipt receipt) {
        byte[] txid = ByteUtils.hexStringToByte(receipt.getTxid());
        byte[] from = ByteUtils.hexStringToByte(receipt.getFrom());
        byte value = 1;
        if (receipt.getAccept() == 1) {
            value = 1;
        } else {
            value = 0;
        }
        byte[] time = ByteUtils.getBytes(receipt.getTimestamp());
        ByteBuffer buffer = ByteBuffer.allocate(txid.length + from.length + 1 + time.length);
        byte[] var10000 = buffer.put(txid).put(from).put(value).put(time).array();
        byte[] hash = Sha256Hash.hash(var10000);

        return ByteUtils.bytesToHexString(hash);

    }

}
