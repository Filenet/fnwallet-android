package io.filenet.wallet.ui.activity

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.view.View
import com.google.gson.Gson
import com.gyf.barlibrary.ImmersionBar
import io.filenet.wallet.BuildConfig
import io.filenet.wallet.R
import io.filenet.wallet.base.BaseActivity
import io.filenet.wallet.entity.ProxyCountResponse
import io.filenet.wallet.entity.ReceiptResponse
import io.filenet.wallet.ui.fragment.FnMineListFragment
import io.filenet.wallet.ui.fragment.FnTransFragment
import io.filenet.wallet.utils.Base58
import io.filenet.wallet.utils.LogUtils
import io.filenet.wallet.utils.ToastUtils
import io.filenet.wallet.utils.WalletDaoUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_fn_new_function.*
import kotlinx.android.synthetic.main.activity_receive_list.*
import kotlinx.android.synthetic.main.common_toolbar.view.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import java.math.BigInteger
import java.util.*
import java.util.concurrent.TimeUnit

class NewFnFunctionActivity : BaseActivity() {

    private var titleList: MutableList<String> = ArrayList()
    private var fragments: MutableList<Fragment> = ArrayList()

    override fun getLayoutId(): Int = R.layout.activity_fn_new_function


    private var fnTotal: String = ""
    private var fnTotalValue: String = ""
    private var fnMeVotes: String = ""
    private var fnProxyVotes: String = ""
    private var fnTotalVotes: String = ""

    private var mDisposable: Disposable? = null


    override fun initToolBar() {
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        mCommonToolbar.iv_back.setImageResource(R.mipmap.app_back_white)
        mCommonToolbar.tv_title.text = WalletDaoUtils.getCurrent().name
        mCommonToolbar.tv_title.setTextColor(ContextCompat.getColor(this, R.color.white))
        mCommonToolbar.lly_back.setOnClickListener {
            onBackPressed()
        }
        ImmersionBar.with(this)
                .titleBar(mCommonToolbar, false)
                .transparentStatusBar()
                .statusBarDarkFont(false, 1f)
                .navigationBarColor(R.color.black)
                .init()

    }

    override fun initDatas() {
        titleList.add(getString(R.string.transaction_record))
        titleList.add(getString(R.string.mining_record))
        fragments.add(FnTransFragment())
        fragments.add(FnMineListFragment())
        val myViewPagerAdapter = MyViewPagerAdapter(supportFragmentManager)
        vp_fn_function.adapter = myViewPagerAdapter
        fnTotal = intent.getStringExtra("fnTotal")
//        fnTotalValue = intent.getStringExtra("fnValue")
        fnMeVotes = intent.getStringExtra("fnMeVotes")
        fnProxyVotes = intent.getStringExtra("fnProxyVotes")
        fnTotalVotes = intent.getStringExtra("fnTotalVotes")
        tv_fn_total.text = fnTotal
//        tv_fn_value.text = fnTotalValue

        tv_votes_me_contract.text = String.format("%.00f FN", BigInteger(fnMeVotes).toDouble() / BigInteger("1000000000").toDouble())
        tv_votes_proxy_contract.text = String.format("%.00f FN", BigInteger(fnProxyVotes).toDouble() / BigInteger("1000000000").toDouble())
        tv_votes_total_contract.text = String.format("%.00f FN", BigInteger(fnTotalVotes).toDouble() / BigInteger("1000000000").toDouble())

    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        Observable.create(ObservableOnSubscribe<String> { e ->
            val client = OkHttpClient().newBuilder().connectTimeout(15, TimeUnit.SECONDS).build()
            val url = BuildConfig.APIHOST + "/proxyTo?address=" + Base58.encode(WalletDaoUtils.getCurrent().address.toLowerCase().substring(2).toByteArray())
            val request = Request.Builder().url(url).get().build()
            val response = client.newCall(request).execute()
            val responseData = response.body()!!.string()
            e.onNext(responseData)
        }).subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {
                        mDisposable = d
                    }

                    override fun onNext(s: String) {
                        LogUtils.e(s)
                        val gson = Gson()
                        try {
                            val receiptResponse: ProxyCountResponse = gson.fromJson(s, ProxyCountResponse::class.java)
                            if (receiptResponse.`object` > 0) {
                                ivHasReceive.visibility = View.VISIBLE
                            } else {
                                ivHasReceive.visibility = View.INVISIBLE
                            }
                        } catch (e: Exception) {
                            ivHasReceive.visibility = View.INVISIBLE
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        ivHasReceive.visibility = View.INVISIBLE
                    }

                    override fun onComplete() {

                    }
                })
    }

    override fun onPause() {
        super.onPause()
        mDisposable?.dispose()
    }

    override fun configViews() {
        tab_fn_function.setupWithViewPager(vp_fn_function)
        ll_fn_vote.setOnClickListener {
            val intent = Intent(this,VoteActivity::class.java)
            startActivity(intent)
        }
        ll_receivables.setOnClickListener {
            val intent = Intent(this, GatheringQRCodeActivity::class.java)
            intent.putExtra("walletAddress", WalletDaoUtils.getCurrent().address)
            intent.putExtra("type", "fn")
            startActivity(intent)
        }
        ll_transfer.setOnClickListener {
            val intent = Intent(this, FnTransferActivity::class.java)
            intent.putExtra("walletId", WalletDaoUtils.getCurrent().id!!.toString())
            intent.putExtra("walletAddress", WalletDaoUtils.getCurrent().address)
            intent.putExtra("walletPwd", WalletDaoUtils.getCurrent().password)
            startActivity(intent)
        }
        ll_contact.setOnClickListener {
            val intent = Intent(this, ContractActivity::class.java)
            startActivity(intent)
        }
        ll_receive.setOnClickListener {
            val intent = Intent(this, ReceiveListActivity::class.java)
            startActivity(intent)

        }
    }


    internal inner class MyViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(i: Int): Fragment {
            return fragments[i]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titleList[position]
        }
    }


}