package io.filenet.wallet.utils;

import android.text.TextUtils;

import java.util.List;

import io.filenet.wallet.ETHTokenApplication;
import io.filenet.wallet.domain.ETHWallet;
import io.filenet.wallet.domain.ETHWalletDao;


public class WalletDaoUtils {
    public static ETHWalletDao ethWalletDao = ETHTokenApplication.getsInstance().getDaoSession().getETHWalletDao();

    public static void insertNewWallet(ETHWallet ethWallet) {
        updateCurrent(-1);
        ethWallet.setCurrent(true);
        ethWalletDao.insert(ethWallet);
    }

    public static ETHWallet updateCurrent(long id) {
        List<ETHWallet> ethWallets = loadAll();
        ETHWallet currentWallet = null;
        for (ETHWallet ethwallet : ethWallets) {
            if (id != -1 && ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                currentWallet = ethwallet;
            } else {
                ethwallet.setCurrent(false);
            }
            ethWalletDao.update(ethwallet);
        }
        return currentWallet;
    }

    public static ETHWallet getCurrent() {
        List<ETHWallet> ethWallets = loadAll();
        ETHWallet mCurrentEthWallte = null;
        if (ethWallets.size() > 0) {
            mCurrentEthWallte = ethWallets.get(0);
        }
        for (ETHWallet ethwallet : ethWallets) {
            if (ethwallet.isCurrent()) {
                mCurrentEthWallte = ethwallet;
            }
        }
        return mCurrentEthWallte;
    }

    public static List<ETHWallet> loadAll() {
        return ethWalletDao.queryBuilder().where(ETHWalletDao.Properties.IsBackup.eq(true)).list();
    }

    public static boolean walletNameChecking(String name) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets) {
            if (TextUtils.equals(ethWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public static ETHWallet setIsBackup(long walletId) {
        ETHWallet ethWallet = ethWalletDao.load(walletId);
        ethWallet.setIsBackup(true);
        ethWalletDao.update(ethWallet);
        return ethWallet;
    }

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    public static void updateWalletName(long walletId, String name) {
        ETHWallet wallet = ethWalletDao.load(walletId);
        wallet.setName(name);
        ethWalletDao.update(wallet);
    }

    public static ETHWallet getWalletByAddress(String address) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets) {
            if ((!TextUtils.isEmpty(ethWallet.getAddress()) && TextUtils.equals(ethWallet.getAddress().trim(), address.trim()))) {
                return ethWallet;
            }
        }
        return null;
    }


    public static void setCurrentAfterDelete() {
        List<ETHWallet> ethWallets = loadAll();
        if (ethWallets != null && ethWallets.size() > 0) {
            ETHWallet ethWallet = ethWallets.get(0);
            ethWallet.setCurrent(true);
            ethWalletDao.update(ethWallet);
        }
    }

    public static boolean walletAddressChecking(String address) {
        for (ETHWallet ethWallet : loadAll()) {
            if (ethWallet.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }
}
