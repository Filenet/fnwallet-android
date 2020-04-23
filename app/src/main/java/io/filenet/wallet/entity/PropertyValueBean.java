package io.filenet.wallet.entity;

public class PropertyValueBean {
    private QueryETH queryETH;
    private EthAmount ethAmount;
    private FnBalanceBean fnBalanceBean;
    private Fn2USDTBean fn2USDTBean;





    public Fn2USDTBean getFn2USDTBean() {
        return fn2USDTBean;
    }

    public void setFn2USDTBean(Fn2USDTBean fn2USDTBean) {
        this.fn2USDTBean = fn2USDTBean;
    }



    public FnBalanceBean getFnBalanceBean() {
        return fnBalanceBean;
    }

    public void setFnBalanceBean(FnBalanceBean fnBalanceBean) {
        this.fnBalanceBean = fnBalanceBean;
    }

    public EthAmount getEthAmount() {
        return ethAmount;
    }

    public void setEthAmount(EthAmount ethAmount) {
        this.ethAmount = ethAmount;
    }

    public QueryETH getQueryETH() {
        return queryETH;
    }

    public void setQueryETH(QueryETH queryETH) {
        this.queryETH = queryETH;
    }

    @Override
    public String toString() {
        return "PropertyValueBean{" +
                "queryETH=" + queryETH +
                ", ethAmount=" + ethAmount +
                ", fnBalanceBean=" + fnBalanceBean +
                ", fn2USDTBean=" + fn2USDTBean +

                '}';
    }
}
