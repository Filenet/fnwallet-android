package io.filenet.wallet.entity;

import java.util.List;

public class FnTrBills {

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

        private PageBean page;


        public PageBean getPage() {
            return page;
        }

        public void setPage(PageBean page) {
            this.page = page;
        }


        public static class PageBean {

            private int total;
            private int size;
            private int current;
            private int pages;
            private List<RecordsBean> list;

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public int getCurrent() {
                return current;
            }

            public void setCurrent(int current) {
                this.current = current;
            }

            public int getPages() {
                return pages;
            }

            public void setPages(int pages) {
                this.pages = pages;
            }

            public List<RecordsBean> getRecords() {
                return list;
            }

            public void setRecords(List<RecordsBean> records) {
                this.list = records;
            }

            public static class RecordsBean {

                private String txid;
                private String from;
                private String to;
                private long value;
                private int height;
                private int timestamp;
                private String hash;
                private String update;
                private int valid;
                private String content;

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public String getId() {
                    return txid;
                }

                public void setId(String id) {
                    this.txid = id;
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

                public long getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getTimestamp() {
                    return timestamp;
                }

                public void setTimestamp(int timestamp) {
                    this.timestamp = timestamp;
                }

                public String getHash() {
                    return hash;
                }

                public void setHash(String hash) {
                    this.hash = hash;
                }

                public String getUpdate() {
                    return update;
                }

                public void setUpdate(String update) {
                    this.update = update;
                }

                public int getValid() {
                    return valid;
                }

                public void setValid(int valid) {
                    this.valid = valid;
                }
            }
        }

        public static class AccountBean {

            private long balance;

            public long getBalance() {
                return balance;
            }

            public void setBalance(long balance) {
                this.balance = balance;
            }
        }
    }
}
