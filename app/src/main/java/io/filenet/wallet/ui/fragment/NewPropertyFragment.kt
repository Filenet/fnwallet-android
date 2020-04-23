package io.filenet.wallet.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import com.google.gson.Gson
import io.filenet.wallet.BuildConfig

import io.filenet.wallet.R
import io.filenet.wallet.base.BaseFragment
import io.filenet.wallet.domain.ETHWallet
import io.filenet.wallet.entity.*
import io.filenet.wallet.event.DeleteWalletSuccessEvent
import io.filenet.wallet.event.UpdatePropertyEvent
import io.filenet.wallet.ui.activity.*
import io.filenet.wallet.ui.adapter.DrawerWalletAdapter
import io.filenet.wallet.ui.contract.PropertyContract
import io.filenet.wallet.ui.presenter.PropertyPresenter
import io.filenet.wallet.utils.*
import io.filenet.wallet.view.ImportWalletWindow
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_new_property.*
import kotlinx.android.synthetic.main.property_drawer_right_layout.*
import kotlinx.android.synthetic.main.property_drawer_right_layout.view.*
import kotlinx.android.synthetic.main.property_fragment_toolbar.*
import okhttp3.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException

import java.math.BigInteger
import java.util.concurrent.TimeUnit
import kotlin.Exception

class NewPropertyFragment : BaseFragment(), PropertyContract.View {

    private var isShouldRefresh: Boolean = false
    private var drawerWalletAdapter: DrawerWalletAdapter? = null
    private var mPresenter: PropertyContract.Presenter? = null
    private var currencyUnit = 0

    var isGetFnTotalFinished = false
    var isGetFnUSDTFinished = false
    var isGetQueryETHFinished = false
    var isGetETHValueFinished = false
    var propertyValueBean: PropertyValueBean = PropertyValueBean()

    override fun showError(errorInfo: String?) {

    }

    override fun showWallet(wallet: ETHWallet) {
        tv_thumb_property.text = wallet.name
        update(Base58.encode(wallet.address.toLowerCase().substring(2).toByteArray()))
    }

    override fun switchWallet(currentPosition: Int, wallet: ETHWallet) {
        drawerWalletAdapter!!.setCurrentWalletPosition(currentPosition)
        openOrCloseDrawerLayout()
        tv_thumb_property.text = wallet.name
        update(Base58.encode(wallet.address.toLowerCase().substring(2).toByteArray()))

    }


    override fun complete() {
    }


    override fun showDrawerWallets(ethWallets: MutableList<ETHWallet>?) {
        drawerWalletAdapter = DrawerWalletAdapter(context, ethWallets, R.layout.list_item_drawer_property_wallet)
        lv_wallet.adapter = drawerWalletAdapter
        lv_wallet.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            mPresenter!!.switchCurrentWallet(position, ethWallets!![position].id!!)
            openOrCloseDrawerLayout()
        }
    }

    override fun showCurrentWalletProrperty(balance: String?) {
    }

    var isPrepared = false

    override fun getLayoutResId(): Int = R.layout.fragment_new_property

    override fun attachView() {
        isPrepared = true
    }

    override fun initDatas() {
        mPresenter = PropertyPresenter(this)
        mPresenter!!.loadAllWallets()
    }

    override fun configViews() {
        drawer.setScrimColor(ContextCompat.getColor(context!!, R.color.property_drawer_scrim_bg_color))
        ll_property_fn.setOnClickListener {
            val intent = Intent(getActivity(), NewFnFunctionActivity::class.java)
            intent.putExtra("fnTotal", fn_total_count.text.trim())
            intent.putExtra("fnValue", fn_total_value.text.toString())
            try {
                intent.putExtra("fnMeVotes", propertyValueBean.fnBalanceBean.data.ownLock ?: "0")
                intent.putExtra("fnProxyVotes", propertyValueBean.fnBalanceBean.data.agentLock ?: "0")
                intent.putExtra("fnTotalVotes", propertyValueBean.fnBalanceBean.data.totalLock ?: "0")
            }catch (e :Exception){
                intent.putExtra("fnMeVotes",  "0")
                intent.putExtra("fnProxyVotes",  "0")
                intent.putExtra("fnTotalVotes",  "0")
            }
            startActivity(intent)
        }
        ll_property_eth.setOnClickListener {
            val intent = Intent(getActivity(), NewETHFunctionActivity::class.java)
            intent.putExtra("ethTotal", eth_total_count.text.trim())
            intent.putExtra("ethValue", eth_total_value.text.toString())
            startActivity(intent)
        }
        lly_wallet_manager.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, WalletDetailActivity::class.java)
            val wallet = WalletDaoUtils.getCurrent() ?: return@OnClickListener
            intent.putExtra("fn_total", total_property.text.toString())
            intent.putExtra("walletId", wallet.id)
            intent.putExtra("walletPwd", wallet.password)
            intent.putExtra("walletAddress", wallet.address)
            intent.putExtra("walletName", wallet.name)
            intent.putExtra("walletMnemonic", wallet.mnemonic)
            intent.putExtra("walletIsBackup", wallet.getIsBackup())
            startActivityForResult(intent, WALLET_DETAIL_REQUEST)
        })
        drawer.lly_import_wallet.setOnClickListener {
            val importWalletWindow = ImportWalletWindow(mContext)
            importWalletWindow.setOnInputDialogButtonClickListener(object : ImportWalletWindow.OnInputDialogButtonClickListener {
                override fun onCancel() {
                    importWalletWindow.dismiss()
                }

                override fun onMnemonic() {
                    importWalletWindow.dismiss()
                    val intent = Intent()
                    intent.setClass(context!!, LoadWalletActivity::class.java)
                    intent.putExtra("import_type", 1)
                    startActivity(intent)
                }

                override fun onPrivatekey() {
                    importWalletWindow.dismiss()
                    val intent = Intent()
                    intent.putExtra("import_type", 2)
                    intent.setClass(context!!, LoadWalletActivity::class.java)
                    startActivity(intent)
                }

                override fun onKeyStore() {
                    importWalletWindow.dismiss()
                    val intent = Intent()
                    intent.putExtra("import_type", 3)
                    intent.setClass(context!!, LoadWalletActivity::class.java)
                    startActivity(intent)
                }
            })
            importWalletWindow.show()
            openOrCloseDrawerLayout()
        }
        lly_menu.setOnClickListener {
            openOrCloseDrawerLayout()
        }
        lly_create_wallet.setOnClickListener {
            val intent = Intent(context, CreateWalletActivity::class.java)
            startActivityForResult(intent, CREATE_WALLET_REQUEST)
            openOrCloseDrawerLayout()
        }
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
            if (userVisibleHint) {
                update(Base58.encode(WalletDaoUtils.getCurrent().address.toLowerCase().substring(2).toByteArray()))
            }
        }


    }
    private fun openOrCloseDrawerLayout() {
        val drawerOpen = drawer.isDrawerOpen(Gravity.END)
        if (drawerOpen) {
            drawer.closeDrawer(Gravity.END)
        } else {
            drawer.openDrawer(Gravity.END)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser && drawer != null) {
            drawer.closeDrawer(Gravity.END)
        }
        if (isVisibleToUser && isPrepared && isShouldRefresh) {
            mPresenter!!.loadAllWallets()
            tv_thumb_property.text = WalletDaoUtils.getCurrent().name
            isShouldRefresh = false
            update(Base58.encode(WalletDaoUtils.getCurrent().address.toLowerCase().substring(2).toByteArray()))
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(wallet: ETHWallet) {
        mPresenter!!.loadAllWallets()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateProperty(updatePropertyEvent: UpdatePropertyEvent) {
        isShouldRefresh = true
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshList(deleteWalletSuccessEvent: DeleteWalletSuccessEvent) {
        WalletDaoUtils.setCurrentAfterDelete()
        mPresenter!!.loadAllWallets()
    }

    companion object {
        const val WALLET_DETAIL_REQUEST = 1104
        const val CREATE_WALLET_REQUEST = 1101
    }


    fun update(address: String) {
        currencyUnit = SharedPreferencesUtil.getInstance().getInt("currencyUnit", 0)
        if (WalletDaoUtils.getCurrent() == null) {
            dismissDialog()
            return
        }
        showDialog("")
        if (currencyUnit != 0) {
            fn_total_value.text = String.format("≈ ¥ %.02f", 0.0)
            eth_total_value.text = String.format("≈ ¥ %.02f", 0.0)
            total_property.text = String.format("≈ ¥ %.02f", 0.0)
        } else {
            fn_total_value.text = String.format("≈$ %.02f", 0.0)
            eth_total_value.text = String.format("≈$ %.02f", 0.0)
            total_property.text = String.format("≈$ %.02f", 0.0)
        }
        fn_total_count.text = String.format("%.02f", 0.0)
        eth_total_count.text = String.format("%.03f", 0.0)
        isGetFnTotalFinished = false
        isGetFnUSDTFinished = false
        isGetQueryETHFinished = false
        isGetETHValueFinished = false
        propertyValueBean = PropertyValueBean()
        getFnBalance(address)
        getETHValue(WalletDaoUtils.getCurrent().address)
        getFnUSDTSymbolObservable()
        getQueryEthObservable()

    }



    @SuppressLint("CheckResult")
    private fun getFnUSDTSymbolObservable() {
        val observable: Observable<PropertyValueBean> = Observable.create {
            val client = OkHttpClient().newBuilder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .build()
            val url = "https://exchange-open-api.coineal.com/open/api/get_ticker?symbol=fnusdt"
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val rStr = response.body()!!.string()
            LogUtils.e(rStr)
            val fn2USDTBean = Gson().fromJson(rStr, Fn2USDTBean::class.java)
            if (fn2USDTBean.code == 0) {
                if (fn2USDTBean.data != null) {
                    propertyValueBean.fn2USDTBean = fn2USDTBean
                    it.onNext(propertyValueBean)
                }
            } else {
                it.onError(Exception("FnEth"))
            }
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    isGetFnUSDTFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(it)
                    }
                }, Consumer {
                    isGetFnUSDTFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                        ToastUtils.showToast(mContext, R.string.network_error)
                    }
                }, Action {
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                    }
                })
    }


    private fun getQueryEthObservable() {
        val observable: Observable<PropertyValueBean> = Observable.create { emitter ->
            val url: String = BuildConfig.APIHOST + "/queryETH"
            val client = OkHttpClient().newBuilder().connectTimeout(10, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build()
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val responseData = response.body()!!.string()
            LogUtils.e(responseData)
            val queryETH = Gson().fromJson(responseData, QueryETH::class.java)
            if (queryETH.status == 200) {
                propertyValueBean.queryETH = queryETH
                emitter.onNext(propertyValueBean)
            } else {
                propertyValueBean.queryETH = null
                emitter.onError(Exception("QueryEth error"))
            }
            emitter.onComplete()
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    isGetQueryETHFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(it)
                    }
                }, Consumer {
                    isGetQueryETHFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                        ToastUtils.showToast(mContext, R.string.network_error)
                    }
                }, Action {
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                    }
                })
    }

    private fun getFnBalance(address: String) {

        val observable: Observable<PropertyValueBean> = Observable.create {
            val client = OkHttpClient().newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()
            val url = BuildConfig.APIHOST + "/blockByFrom?addars=$address"
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val rStr = response.body()!!.string()
            var fnBalanceBean = Gson().fromJson(rStr, FnBalanceBean::class.java)
            if (fnBalanceBean.status == 200) {
                if (fnBalanceBean.data != null) {
                    propertyValueBean.fnBalanceBean = fnBalanceBean
                    it.onNext(propertyValueBean)
                } else {
                    val dataBean = FnBalanceBean.DataBean()
                    fnBalanceBean.data = dataBean
                    propertyValueBean.fnBalanceBean = fnBalanceBean
                    it.onNext(propertyValueBean)
                }
            } else {
                propertyValueBean.fnBalanceBean = null
                it.onError(Exception("fnBalanceBean Exception"))
            }
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    isGetFnTotalFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(it)
                    }
                }, Consumer {
                    isGetFnTotalFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                        ToastUtils.showToast(mContext, R.string.network_error)
                    }
                }, Action {
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                    }
                })
    }

    private fun getETHValue(address: String){
        val observable: Observable<PropertyValueBean> = Observable.create {
            val client = OkHttpClient().newBuilder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()
            val url = BuildConfig.APIHOST + "/ETH?json=http%3A%2F%2Fapi.etherscan.io%2Fapi%3Fmodule%3Daccount%26action%3Dbalance%26address%3D" +
                    address + "%26tag%3Dlatest%26apikey%3DHYAHB34F8C34ZXDIJ1K9WVQ7PG1KIZX1SE"
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val rStr = response.body()!!.string()
            var ethAmount = Gson().fromJson(rStr, EthAmount::class.java)
            if (ethAmount.status == 1) {
                propertyValueBean.ethAmount = ethAmount
                it.onNext(propertyValueBean)
            } else {
                it.onError(Exception("fnBalanceBean Exception"))
            }
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer {
                    isGetETHValueFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(it)
                    }
                }, Consumer {
                    isGetETHValueFinished = true
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                        ToastUtils.showToast(mContext, R.string.network_error)
                    }
                }, Action {
                    if (isGetFnTotalFinished && isGetFnUSDTFinished && isGetQueryETHFinished && isGetETHValueFinished) {
                        hideDialog()
                        setPropertyValue(propertyValueBean)
                    }
                })
    }

    private fun setPropertyValue(mPropertyValueBean: PropertyValueBean) {
        var fnBalance = 0.0
        var ethBalance = 0.0
        if (mPropertyValueBean.fnBalanceBean != null&&mPropertyValueBean.ethAmount != null) {
            val balance = mPropertyValueBean.fnBalanceBean.data.balance
            val eth = mPropertyValueBean.ethAmount.result
            if (TextUtils.isEmpty(balance)) {
                fn_total_count.text = String.format("%.02f FN", 0.0)
            } else {
                val wei = BigInteger(balance)
                fnBalance = wei.toDouble() / BigInteger("1000000000").toDouble()
                fn_total_count.text = String.format("%.02f FN", fnBalance)
            }
            if(TextUtils.isEmpty(eth)){
                eth_total_count.text = String.format("%.03f ETH", 0.0)
            }else{
                val wei = BigInteger(eth)
                ethBalance = wei.toDouble() / BigInteger("1000000000000000000").toDouble()
                eth_total_count.text = String.format("%.03f ETH", ethBalance)
            }
            if (mPropertyValueBean.fn2USDTBean != null) {
                if (currencyUnit != 0) {
                    if (mPropertyValueBean.queryETH != null) {
                        val usdt2cny = mPropertyValueBean.queryETH.data.cnyPrice / mPropertyValueBean.queryETH.data.usdPrice
                        val args = mPropertyValueBean.fn2USDTBean.data.last * fnBalance * usdt2cny
                        fn_total_value.text = String.format("≈ ¥ %.02f", args)

                        val eth2cny = mPropertyValueBean.queryETH.data.cnyPrice * (eth.toDouble() / BigInteger("1000000000000000000").toDouble())
                        eth_total_value.text = String.format("≈ ¥ %.02f", eth2cny)

                        val mountcny = args + eth2cny
                        total_property.text = String.format("≈ ¥ %.02f", mountcny)
                    } else {
                        fn_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                        eth_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                        total_property.text = String.format("≈ ¥ %.02f", 0.0)
                    }
                } else {
                    val format = String.format("≈$ %.02f", mPropertyValueBean.fn2USDTBean.data.last * fnBalance)
                    fn_total_value.text = format
                    val eth2usd = mPropertyValueBean.queryETH.data.usdPrice * (eth.toDouble() / BigInteger("1000000000000000000").toDouble())
                    eth_total_value.text = String.format("≈$ %.02f", eth2usd)

                    val mountusd = (mPropertyValueBean.fn2USDTBean.data.last * fnBalance) + eth2usd
                    total_property.text = String.format("≈$ %.02f", mountusd)
                }
            } else {
                if (currencyUnit != 0) {
                    fn_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                    eth_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                    total_property.text = String.format("≈ ¥ %.02f", 0.0)
                } else {
                    fn_total_value.text = String.format("≈$ %.02f", 0.0)
                    eth_total_value.text = String.format("≈$ %.02f", 0.0)
                    total_property.text = String.format("≈$ %.02f", 0.0)
                }
            }

        } else {
            fn_total_count.text = String.format("%.02f FN", 0.0)
            eth_total_count.text = String.format("%.03f ETH", 0.0)
            if (currencyUnit != 0) {
                fn_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                eth_total_value.text = String.format("≈ ¥ %.02f", 0.0)
                total_property.text = String.format("≈ ¥ %.02f", 0.0)
            } else {
                fn_total_value.text = String.format("≈$ %.02f", 0.0)
                eth_total_value.text = String.format("≈$ %.02f", 0.0)
                total_property.text = String.format("≈$ %.02f", 0.0)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WALLET_DETAIL_REQUEST) {
            mPresenter!!.loadAllWallets()
        }
    }

}
