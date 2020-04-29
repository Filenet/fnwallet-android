package io.filenet.wallet.event;

public class ScanResultEvent {

    public ScanResultEvent(String scaResult) {
        this.scaResult = scaResult;
    }

    private String scaResult ;

    public String getScaResult() {
        return scaResult;
    }

    public void setScaResult(String scaResult) {
        this.scaResult = scaResult;
    }
}
