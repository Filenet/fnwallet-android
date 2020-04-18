package io.filenet.wallet.entity;

import java.io.Serializable;

public class VoteContent implements Serializable {
    private String txid;
    private String from;
    private String to;
    private Long value;
    private Long timestamp;
    private Long expired;
    private String sign;
    private Long height;
    private Long expiredHeight;
    private String state;
    private String content;
    private int accept = 1;

    private String month;
    private int addressState;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getAddressState() {
        return addressState;
    }

    public void setAddressState(int addressState) {
        this.addressState = addressState;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public VoteContent() {
    }

    public VoteContent(String txid, String from, String to, Long value, Long timestamp, Long expired, String sign, Long height, Long expiredHeight, String state) {
        this.txid = txid;
        this.from = from;
        this.to = to;
        this.value = value;
        this.timestamp = timestamp;
        this.expired = expired;
        this.sign = sign;
        this.height = height;
        this.expiredHeight = expiredHeight;
        this.state = state;
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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpired() {
        return expired;
    }

    public void setExpired(Long expired) {
        this.expired = expired;
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

    public Long getExpiredHeight() {
        return expiredHeight;
    }

    public void setExpiredHeight(Long expiredHeight) {
        this.expiredHeight = expiredHeight;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
