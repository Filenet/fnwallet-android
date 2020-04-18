package io.filenet.wallet.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.MnemonicException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import io.filenet.wallet.domain.ETHWallet;


public class  ETHWalletUtils {

    private static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();
    private Credentials credentials;
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";
    public static String ETH_LEDGER_TYPE = "m/44'/60'/0'/0";
    public static String ETH_CUSTOM_TYPE = "m/44'/60'/1'/0/0";

    public static ETHWallet generateMnemonic(String walletName, String pwd) {
        String[] pathArray = ETH_JAXX_TYPE.split("/");
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return generateWalletByMnemonic(walletName, ds, pathArray, pwd);
    }

    public static ETHWallet importMnemonic(String path, List<String> list, String pwd) throws MnemonicException {
        if (!path.startsWith("m") && !path.startsWith("M")) {
            return null;
        }
        String[] pathArray = path.split("/");
        if (pathArray.length <= 1) {
            return null;
        }
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);
        ds.check();
        return generateWalletByMnemonic(generateNewWalletName(), ds, pathArray, pwd);
    }

    private static String generateNewWalletName() {
        char letter1 = (char) (int) (Math.random() * 26 + 97);
        char letter2 = (char) (int) (Math.random() * 26 + 97);
        String walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-NewWallet";
        while (WalletDaoUtils.walletNameChecking(walletName)) {
            letter1 = (char) (int) (Math.random() * 26 + 97);
            letter2 = (char) (int) (Math.random() * 26 + 97);
            walletName = String.valueOf(letter1) + String.valueOf(letter2) + "-NewWallet";
        }
        return walletName;
    }

    public static ETHWallet generateWalletByMnemonic(String walletName, DeterministicSeed ds,
                                                     String[] pathArray, String pwd) {
        byte[] seedBytes = ds.getSeedBytes();
        List<String> mnemonic = ds.getMnemonicCode();
        if (seedBytes == null)
            return null;
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        ETHWallet ethWallet = generateWallet(generateNewWalletName(), pwd, keyPair);
        if (ethWallet != null) {
            ethWallet.setMnemonic(convertMnemonicList(mnemonic));
        }
        return ethWallet;
    }

    private static String convertMnemonicList(List<String> mnemonics) {
        StringBuilder sb = new StringBuilder();
        for (String mnemonic : mnemonics
        ) {
            sb.append(mnemonic);
            sb.append(" ");
        }
        return sb.toString();
    }

    private static ETHWallet generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair) {
        WalletFile walletFile;
        try {
            walletFile = Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger publicKey = ecKeyPair.getPublicKey();
        String s = publicKey.toString();
        String wallet_dir = AppFilePath.Wallet_DIR;
        File destination = new File(wallet_dir, "keystore_" + walletName + System.currentTimeMillis() + ".json");
        if (!createParentDir(destination)) {
            return null;
        }
        try {
            objectMapper.writeValue(destination, walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ETHWallet ethWallet = new ETHWallet();
        ethWallet.setName(walletName);
        ethWallet.setAddress(Keys.toChecksumAddress(walletFile.getAddress()));
        ethWallet.setKeystorePath(destination.getAbsolutePath());
        ethWallet.setPassword(Md5Utils.md5(pwd));
        ethWallet.setPrivateKey(ecKeyPair.getPrivateKey().toString(16));
        return ethWallet;
    }

    private static ETHWallet generateWallet(String walletName, String pwd, ECKeyPair ecKeyPair, String keystorePath) {
        WalletFile walletFile;
        try {
            walletFile = Wallet.create(pwd, ecKeyPair, 1024, 1); // WalletUtils. .generateNewWalletFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        BigInteger publicKey = ecKeyPair.getPublicKey();
        String s = publicKey.toString();
        File destination = new File(keystorePath);
        if (!createParentDir(destination)) {
            return null;
        }
        try {
            objectMapper.writeValue(destination, walletFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ETHWallet ethWallet = new ETHWallet();
        ethWallet.setName(walletName);
        ethWallet.setAddress(Keys.toChecksumAddress(walletFile.getAddress()));
        ethWallet.setKeystorePath(destination.getAbsolutePath());
        ethWallet.setPassword(Md5Utils.md5(pwd));
        ethWallet.setPrivateKey(ecKeyPair.getPrivateKey().toString(16));
        return ethWallet;
    }

    public static ETHWallet loadWalletByKeystore(String keystore, String pwd) {
        Credentials credentials = null;
        try {
            long time1 = System.currentTimeMillis();
            WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
            ECKeyPair decrypt = Wallet.decrypt(pwd, walletFile);
            credentials = Credentials.create(decrypt);
            long time2 = System.currentTimeMillis();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (credentials != null) {
            return generateWallet(generateNewWalletName(), pwd, credentials.getEcKeyPair());
        }
        return null;
    }

    public static ETHWallet loadWalletByPrivateKey(String privateKey, String pwd) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(generateNewWalletName(), pwd, ecKeyPair);
    }

    public static ETHWallet changePwdByPrivateKey(String privateKey, String pwd, String keystorePath) {
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
        return generateWallet(generateNewWalletName(), pwd, ecKeyPair, keystorePath);
    }


    private static boolean createParentDir(File file) {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        return true;
    }


    public static ETHWallet modifyPasswordById(long walletId, String walletName, String newPassword) {
        ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);
        ETHWallet ethWalletNew = changePwdByPrivateKey(ethWallet.getPrivateKey(), newPassword, ethWallet.getKeystorePath());
        ethWalletNew.setId(ethWallet.getId());
        ethWalletNew.setName(walletName);
        ethWalletNew.setIsCurrent(ethWallet.isCurrent());
        ethWalletNew.setAddress(ethWallet.getAddress());
        ethWalletNew.setBackup(ethWallet.isBackup());
        ethWalletNew.setMnemonic(ethWallet.getMnemonic());
        WalletDaoUtils.ethWalletDao.insertOrReplace(ethWalletNew);
        return ethWalletNew;
    }

    public static String derivePrivateKey(long walletId, String pwd) {
        ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);
        Credentials credentials;
        ECKeyPair keypair;
        String privateKey = "";
        try {
            credentials = WalletUtils.loadCredentials(pwd, ethWallet.getKeystorePath());
            keypair = credentials.getEcKeyPair();
            privateKey = Numeric.toHexStringNoPrefixZeroPadded(keypair.getPrivateKey(), Keys.PRIVATE_KEY_LENGTH_IN_HEX);
        } catch (CipherException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    public static String deriveKeystore(long walletId, String pwd) {
        ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);
        String keystore = null;
        WalletFile walletFile;
        try {
            walletFile = objectMapper.readValue(new File(ethWallet.getKeystorePath()), WalletFile.class);
            keystore = objectMapper.writeValueAsString(walletFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keystore;
    }

    public static boolean deleteWallet(long walletId) {
        ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);
        WalletDaoUtils.ethWalletDao.deleteByKey(walletId);
        if (deleteFile(ethWallet.getKeystorePath())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static ETHWallet modifyPassword(long walletId, String walletName, String oldPassword, String newPassword) {
        ETHWallet ethWallet = WalletDaoUtils.ethWalletDao.load(walletId);

        String mPrivateKey = derivePrivateKey(walletId, oldPassword);

        ETHWallet ethWalletNew = changePwdByPrivateKey(mPrivateKey, newPassword, ethWallet.getKeystorePath());

        ethWalletNew.setId(ethWallet.getId());
        ethWalletNew.setName(walletName);
        ethWalletNew.setIsCurrent(ethWallet.isCurrent());
        ethWalletNew.setAddress(ethWallet.getAddress());
        ethWalletNew.setBackup(ethWallet.isBackup());
        WalletDaoUtils.ethWalletDao.insertOrReplace(ethWalletNew);

        return ethWalletNew;
    }
}
