package io.filenet.wallet.entity;

public class EthAmount {
    private int status;
    private String message;
    private String result;


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "EthAmount{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
