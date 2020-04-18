package io.filenet.wallet.domain;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;


@Entity
public class ETHWallet {
    @Id(autoincrement = true)
    private Long id;

    private String address;
    private String name;
    private String password;
    private String keystorePath;

    private boolean isCurrent;
    private boolean isBackup;
    @Transient
    private String mnemonic;
    @Transient
    private String privateKey;


    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public ETHWallet(Long aLong, Object p1, Object p2, Object p3, Object p4, Object p5, boolean b, boolean b1) {
    }

    @Generated(hash = 1982820589)
    public ETHWallet(Long id, String address, String name, String password, String keystorePath,
            boolean isCurrent, boolean isBackup) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.password = password;
        this.keystorePath = keystorePath;
        this.isCurrent = isCurrent;
        this.isBackup = isBackup;
    }

    @Generated(hash = 1963897189)
    public ETHWallet() {
    }

    public boolean isBackup() {
        return isBackup;
    }

    public void setBackup(boolean backup) {
        isBackup = backup;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean getIsCurrent() {
        return this.isCurrent;
    }

    public void setIsCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public boolean getIsBackup() {
        return this.isBackup;
    }

    public void setIsBackup(boolean isBackup) {
        this.isBackup = isBackup;
    }

    @Override
    public String toString() {
        return "ETHWallet{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", isCurrent=" + isCurrent +
                ", isBackup=" + isBackup +
                ", ethamount=" + '}';
    }



    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
