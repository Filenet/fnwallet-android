package io.filenet.wallet.entity;

public class Fn2USDTBean {


    private int code;
    private String msg;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public class DataBean {
        private double amount;
        private double high;
        private double vol;
        private double last;
        private double low;
        private double sell;
        private long time;

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public double getHigh() {
            return high;
        }

        public void setHigh(double high) {
            this.high = high;
        }

        public double getVol() {
            return vol;
        }

        public void setVol(double vol) {
            this.vol = vol;
        }

        public double getLast() {
            return last;
        }

        public void setLast(double last) {
            this.last = last;
        }

        public double getLow() {
            return low;
        }

        public void setLow(double low) {
            this.low = low;
        }

        public double getSell() {
            return sell;
        }

        public void setSell(double sell) {
            this.sell = sell;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "amount=" + amount +
                    ", high=" + high +
                    ", vol=" + vol +
                    ", last=" + last +
                    ", low=" + low +
                    ", sell=" + sell +
                    ", time=" + time +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Fn2USDTBean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
