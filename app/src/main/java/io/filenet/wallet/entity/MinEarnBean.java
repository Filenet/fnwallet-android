package io.filenet.wallet.entity;

import java.io.Serializable;
import java.util.List;

public class MinEarnBean {
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
        long total;
        long current;
        int pages;
        int size;
        List<BlocksBean> records;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public long getCurrent() {
            return current;
        }

        public void setCurrent(long current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public List<BlocksBean> getRecords() {
            return records;
        }

        public void setRecords(List<BlocksBean> records) {
            this.records = records;
        }


        public class BlocksBean implements Serializable {
            private long timestamp;
            private String height;
            private String total;
            private String reward;
            private String tcount;
            private String length;
            private String ver;
            private String miner;
            private String transactions;
            private String prevhash;
            private String hash;
            private Object transfers;
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }

            public String getHeight() {
                return height;
            }

            public void setHeight(String height) {
                this.height = height;
            }

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public String getReward() {
                return reward;
            }

            public void setReward(String reward) {
                this.reward = reward;
            }

            public String getTcount() {
                return tcount;
            }

            public void setTcount(String tcount) {
                this.tcount = tcount;
            }

            public String getLength() {
                return length;
            }

            public void setLength(String length) {
                this.length = length;
            }

            public String getVer() {
                return ver;
            }

            public void setVer(String ver) {
                this.ver = ver;
            }

            public String getMiner() {
                return miner;
            }

            public void setMiner(String miner) {
                this.miner = miner;
            }

            public String getTransactions() {
                return transactions;
            }

            public void setTransactions(String transactions) {
                this.transactions = transactions;
            }

            public String getPrevHash() {
                return prevhash;
            }

            public void setPrevHash(String prevHash) {
                this.prevhash = prevHash;
            }

            public String getHash() {
                return hash;
            }

            public void setHash(String hash) {
                this.hash = hash;
            }

            public Object getTransfers() {
                return transfers;
            }

            public void setTransfers(Object transfers) {
                this.transfers = transfers;
            }
        }
    }

}
