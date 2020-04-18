package io.filenet.wallet.utils;

import java.nio.ByteBuffer;

public class FnWalletUtils {

    public static byte[] dataAppend(String from, long toCount, long value, long timestamp) {
        byte[] from1 = ByteUtils.hexStringToByte(from);
        byte[] tc = ByteUtils.getBytes(toCount);
        byte[] va = ByteUtils.getBytes(value);
        byte[] ti = ByteUtils.getBytes(timestamp);
        ByteBuffer buffer = ByteBuffer.allocate(from1.length + tc.length + va.length + ti.length);
        byte[] resultBytes = buffer.put(from1).put(tc).put(va).put(ti).array();
        return resultBytes;
    }

    public static byte[] dataAppend(byte[] headStr, String from, long toCount) {

        byte[] from1 = ByteUtils.hexStringToByte(from);
        byte[] tc = ByteUtils.getBytes(toCount);
        ByteBuffer buffer = ByteBuffer.allocate(headStr.length + from1.length + tc.length);
        byte[] resultBytes = buffer.put(headStr).put(from1).put(tc).array();

        return resultBytes;
    }

    public static byte[] dataAppendReceipt(String txid, String from, long acce, long toCount) {
        byte[] from1 = ByteUtils.hexStringToByte(from);
        byte[] txid1 = ByteUtils.hexStringToByte(txid);
        byte[] acce1 = ByteUtils.getBytes(acce);
        byte[] tc = ByteUtils.getBytes(toCount);
        ByteBuffer buffer = ByteBuffer.allocate(txid1.length + from1.length + acce1.length + tc.length);
        byte[] var10000 = buffer.put(from1).put(txid1).put(acce1).put(tc).array();
        return var10000;
    }

}
