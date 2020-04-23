package io.filenet.wallet.entity;

public class FnBalanceBean {

    private int errorCode;
    private boolean success;
    private String error;
    private DataBean data;
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {

        private String balance;
        private String approvebalance;
        private String proxyaddress;
        private String proxybalance;
        private String ownLock;
        private String agentLock;
        private String totalLock;

        public String getApprovebalance() {
            return approvebalance;
        }

        public void setApprovebalance(String approvebalance) {
            this.approvebalance = approvebalance;
        }

        public String getProxyaddress() {
            return proxyaddress;
        }

        public void setProxyaddress(String proxyaddress) {
            this.proxyaddress = proxyaddress;
        }

        public String getProxybalance() {
            return proxybalance;
        }

        public void setProxybalance(String proxybalance) {
            this.proxybalance = proxybalance;
        }

        public String getOwnLock() {
            return ownLock;
        }

        public void setOwnLock(String ownLock) {
            this.ownLock = ownLock;
        }

        public String getAgentLock() {
            return agentLock;
        }

        public void setAgentLock(String agentLock) {
            this.agentLock = agentLock;
        }

        public String getTotalLock() {
            return totalLock;
        }

        public void setTotalLock(String totalLock) {
            this.totalLock = totalLock;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "balance='" + balance + '\'' +
                    ", approvebalance='" + approvebalance + '\'' +
                    ", proxyaddress='" + proxyaddress + '\'' +
                    ", proxybalance='" + proxybalance + '\'' +
                    ", ownLock='" + ownLock + '\'' +
                    ", agentLock='" + agentLock + '\'' +
                    ", totalLock='" + totalLock + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FnBalanceBean{" +
                "errorCode=" + errorCode +
                ", success=" + success +
                ", error='" + error + '\'' +
                ", data=" + data.toString() +
                ", status=" + status +
                '}';
    }
}
