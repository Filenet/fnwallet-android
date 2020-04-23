package io.filenet.wallet.entity;

public class QueryETH {
    private int status;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private double cnyPrice;
        private double usdPrice;

        public double getCnyPrice() {
            return cnyPrice;
        }

        public void setCnyPrice(double cnyPrice) {
            this.cnyPrice = cnyPrice;
        }

        public double getUsdPrice() {
            return usdPrice;
        }

        public void setUsdPrice(double usdPrice) {
            this.usdPrice = usdPrice;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "cnyPrice=" + cnyPrice +
                    ", usdPrice=" + usdPrice +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QueryETH{" +
                "status=" + status +
                ", data=" + data.toString() +
                '}';
    }
}
