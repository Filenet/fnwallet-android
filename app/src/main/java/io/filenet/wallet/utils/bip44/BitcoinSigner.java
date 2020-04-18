package io.filenet.wallet.utils.bip44;


public interface BitcoinSigner {
   byte[] makeStandardBitcoinSignature(Sha256Hash transactionSigningHash);
}
