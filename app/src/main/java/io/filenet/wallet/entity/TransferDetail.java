package io.filenet.wallet.entity;

import java.io.Serializable;

public class TransferDetail implements Serializable {

    private Long id;
    private String txid;
    private String to;
    private Long value;

    public TransferDetail() {
    }

    public TransferDetail(Long id, String txid, String to, Long value) {
        this.id = id;
        this.txid = txid;
        this.to = to;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
