package io.filenet.wallet.entity;


import java.io.Serializable;

public class Fnv2Block implements Serializable {

    private Long height;
    private Long total;
    private Long reward;
    private Long timestamp;
    private Long transfercount;
    private Long approvecount;
    private Long proxycount;
    private Long rxcount;
    private String seed;
    private String txroot;
    private String rxroot;
    private String cxhash;
    private String miner;
    private String extra;
    private String prevhash;
    private String hash;
    private String sign;

    private Long transfer;
    private Long proxy;
    private Long receipt;
    private Long approve;

    public Fnv2Block() {
    }


    public Fnv2Block(Long height, Long total, Long reward, Long timestamp, Long transfercount, Long approvecount, Long proxycount, Long rxcount, String seed, String txroot, String rxroot, String cxhash, String miner, String extra, String prevhash, String hash, String sign, Long transfer, Long proxy, Long receipt, Long approve) {
        this.height = height;
        this.total = total;
        this.reward = reward;
        this.timestamp = timestamp;
        this.transfercount = transfercount;
        this.approvecount = approvecount;
        this.proxycount = proxycount;
        this.rxcount = rxcount;
        this.seed = seed;
        this.txroot = txroot;
        this.rxroot = rxroot;
        this.cxhash = cxhash;
        this.miner = miner;
        this.extra = extra;
        this.prevhash = prevhash;
        this.hash = hash;
        this.sign = sign;
        this.transfer = transfer;
        this.proxy = proxy;
        this.receipt = receipt;
        this.approve = approve;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getReward() {
        return reward;
    }

    public void setReward(Long reward) {
        this.reward = reward;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTransfercount() {
        return transfercount;
    }

    public void setTransfercount(Long transfercount) {
        this.transfercount = transfercount;
    }

    public Long getApprovecount() {
        return approvecount;
    }

    public void setApprovecount(Long approvecount) {
        this.approvecount = approvecount;
    }

    public Long getProxycount() {
        return proxycount;
    }

    public void setProxycount(Long proxycount) {
        this.proxycount = proxycount;
    }

    public Long getRxcount() {
        return rxcount;
    }

    public void setRxcount(Long rxcount) {
        this.rxcount = rxcount;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getTxroot() {
        return txroot;
    }

    public void setTxroot(String txroot) {
        this.txroot = txroot;
    }

    public String getRxroot() {
        return rxroot;
    }

    public void setRxroot(String rxroot) {
        this.rxroot = rxroot;
    }

    public String getCxhash() {
        return cxhash;
    }

    public void setCxhash(String cxhash) {
        this.cxhash = cxhash;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getPrevhash() {
        return prevhash;
    }

    public void setPrevhash(String prevhash) {
        this.prevhash = prevhash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getTransfer() {
        return transfer;
    }

    public void setTransfer(Long transfer) {
        this.transfer = transfer;
    }

    public Long getProxy() {
        return proxy;
    }

    public void setProxy(Long proxy) {
        this.proxy = proxy;
    }

    public Long getReceipt() {
        return receipt;
    }

    public void setReceipt(Long receipt) {
        this.receipt = receipt;
    }

    public Long getApprove() {
        return approve;
    }

    public void setApprove(Long approve) {
        this.approve = approve;
    }
}
