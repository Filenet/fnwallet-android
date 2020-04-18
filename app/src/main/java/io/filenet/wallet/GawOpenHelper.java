package io.filenet.wallet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;

import io.filenet.wallet.domain.DaoMaster;
import io.filenet.wallet.domain.ETHWalletDao;

public class GawOpenHelper extends DaoMaster.DevOpenHelper {

    public GawOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int currentVersion, int lastestVersion) {
        MigrationHelper.migrate(db, ETHWalletDao.class);
    }
}
