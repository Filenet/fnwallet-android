package io.filenet.wallet.entity;

import java.io.Serializable;

public class Receipt implements Serializable {
    private String rxid;
    private String txid;
    private String from;
    private int accept;
    private Long value;
    private Long timestamp;
    private String sign;
    private Long height;
    private String content;

    private String acceptcontent;




    public Receipt() {
    }


    public int isAccept() {
        return accept;
    }

    public String getAcceptcontent() {
        return acceptcontent;
    }

    public void setAcceptcontent(String acceptcontent) {
        this.acceptcontent = acceptcontent;
    }

    //private String password;

    public String to;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public String getRxid() {
        return rxid;
    }

    public void setRxid(String rxid) {
        this.rxid = rxid;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getfrom() {
        return from;
    }

    public void setfrom(String from) {
        this.from = from;
    }


    public int getAccept() {
        return accept;
    }

    public void setAccept(int accept) {
        this.accept = accept;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
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
        return "Receipt{" +
                "rxid='" + rxid + '\'' +
                ", txid='" + txid + '\'' +
                ", from='" + from + '\'' +
                ", accept=" + accept +
                ", value=" + value +
                ", timestamp=" + timestamp +
                ", sign='" + sign + '\'' +
                ", height=" + height +
                ", content='" + content + '\'' +
                ", acceptcontent='" + acceptcontent + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
