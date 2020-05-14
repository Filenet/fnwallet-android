package io.filenet.wallet.entity;


import java.io.Serializable;

public class ReceiptAndProxy implements Serializable {

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
    private int accept;
    private String content;


    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    private String rxid;


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

    public String getRxid() {
        return rxid;
    }

    public void setRxid(String rxid) {
        this.rxid = rxid;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ReceiptAndProxy{" +
                "txid='" + txid + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", expired=" + expired +
                ", sign='" + sign + '\'' +
                ", height=" + height +
                ", expiredHeight=" + expiredHeight +
                ", state='" + state + '\'' +
                ", rxid='" + rxid + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
