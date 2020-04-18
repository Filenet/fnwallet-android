package io.filenet.wallet.utils;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;

public class SignatureUtils {

    public static String signMsg(byte[] data, String privateKey) {
        byte[] pri = ByteUtils.hexStringToByte(privateKey);
        ECKey ecKey = ECKey.fromPrivate(pri);
        Sha256Hash hash = Sha256Hash.of(data);
        ECKey.ECDSASignature sign = ecKey.sign(hash);
        byte[] sigData = new byte[64];
        System.arraycopy(Utils.bigIntegerToBytes(sign.r, 32), 0, sigData, 0, 32);
        System.arraycopy(Utils.bigIntegerToBytes(sign.s, 32), 0, sigData, 32, 32);
        return ByteUtils.bytesToHexString(sigData);
    }
}
