package io.filenet.wallet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.List;

public class Transfer implements Serializable {
    private String txid;
    private String from;
    private Long tocount;
    private Long value;
    private Long timestamp;
    private List<TransferDetail> transferdetails;
    @JsonIgnore
    private String sign;
    private Long height;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Transfer() {
    }

    public Transfer(String txid, String from, Long tocount, Long value, Long timestamp, List<TransferDetail> transferdetails, String sign, Long height) {
        this.txid = txid;
        this.from = from;
        this.tocount = tocount;
        this.value = value;
        this.timestamp = timestamp;
        this.transferdetails = transferdetails;
        this.sign = sign;
        this.height = height;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getTocount() {
        return tocount;
    }

    public void setTocount(Long tocount) {
        this.tocount = tocount;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<TransferDetail> getTransferdetails() {
        return transferdetails;
    }

    public void setTransferdetails(List<TransferDetail> transferdetails) {
        this.transferdetails = transferdetails;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Long getHeight() {
        return height;
    }

    public void setHeight(Long height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "txid='" + txid + '\'' +
                ", from='" + from + '\'' +
                ", tocount=" + tocount +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", transferdetails=" + transferdetails +
                ", sign='" + sign + '\'' +
                ", height=" + height +
                '}';
    }
}
