package io.filenet.wallet.ui.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.filenet.wallet.BuildConfig
import io.filenet.wallet.R
import io.filenet.wallet.base.BaseActivity
import io.filenet.wallet.entity.FnBalanceBean
import io.filenet.wallet.entity.Proxy
import io.filenet.wallet.entity.ReceiptDetailResponse
import io.filenet.wallet.entity.VoteContent
import io.filenet.wallet.utils.*
import io.filenet.wallet.view.ContainsEmojiEditText
import io.filenet.wallet.view.InputPwdDialog
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_contract.*
import kotlinx.android.synthetic.main.activity_contract.btn_confirm
import kotlinx.android.synthetic.main.activity_contract.edtTransferCount
import kotlinx.android.synthetic.main.activity_contract.fnCount
import kotlinx.android.synthetic.main.activity_contract.ivQrScanner
import kotlinx.android.synthetic.main.activity_vote.*
import kotlinx.android.synthetic.main.common_toolbar.view.*
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class VoteActivity :BaseActivity() {
    override fun getLayoutId(): Int = R.layout.activity_vote

    private val QRCODE_SCANNER_REQUEST = 1100
    private var fnBalance: Double = 0.toDouble()
    private val inputPwdDialog: InputPwdDialog by lazy {
        InputPwdDialog(this)
    }
    override fun initToolBar() {
        mCommonToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        mCommonToolbar.tv_title.text = getString(R.string.vote)
        mCommonToolbar.rl_btn.visibility = View.VISIBLE
        mCommonToolbar.iv_back.setImageResource(R.mipmap.app_back_black)
        mCommonToolbar.tv_title.setTextColor(ContextCompat.getColor(this, R.color.color_333333))
        mCommonToolbar.iv_btn.setImageResource(R.mipmap.ic_contract_order)
        mCommonToolbar.rl_btn.setOnClickListener {
            val intent = Intent(this, VoteBillActivity::class.java)
            startActivity(intent)
        }
        ivQrScanner.setOnClickListener {
            RxPermissions(this)
                    .requestEach(Manifest.permission.CAMERA)
                    .subscribe(Consumer<Permission> { permission ->
                        when {
                            permission.granted -> {
                                val intent = Intent(this, QRCodeScannerActivity::class.java)
                                startActivityForResult(intent, QRCODE_SCANNER_REQUEST)
                            }
                            permission.shouldShowRequestPermissionRationale -> AlertDialog.Builder(this)
                                    .setTitle(R.string.string_apply)
                                    .setMessage(R.string.apply_camera_access)
                                    .setPositiveButton(R.string.to_setting) { dialog, which ->
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = Uri.fromParts("package", applicationContext.packageName, null)
                                        intent.data = uri
                                        startActivity(intent)
                                    }
                                    .setNegativeButton(R.string.cancel) { dialog, which ->
                                    }.setOnCancelListener {
                                    }.show()
                            else -> AlertDialog.Builder(this)
                                    .setTitle(R.string.string_apply)
                                    .setMessage(R.string.apply_camera_access)
                                    .setPositiveButton(R.string.to_setting) { dialog, which ->
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = Uri.fromParts("package", applicationContext.packageName, null)
                                        intent.data = uri
                                        startActivity(intent)
                                    }
                                    .setNegativeButton(R.string.cancel) { dialog, which ->
                                    }.setOnCancelListener {
                                    }.show()
                        }
                    })
        }

        btn_confirm.setOnClickListener {
            if (TextUtils.isEmpty(edtVoteAddress.text.toString().trim())) {
                ToastUtils.showToast(mContext, R.string.vote_address_is_empty)
                return@setOnClickListener
            }
            val bytes = Base58.decode(edtVoteAddress.text.toString())
            if (bytes == null || bytes.size != 40) {
                ToastUtils.showLongToast(mContext, R.string.vote_address_is_error)
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(edtTransferCountVote.text.toString())) {
                ToastUtils.showLongToast(mContext, R.string.qingshuruzhuanzhangjine)
                return@setOnClickListener
            }
            if ("." == edtTransferCountVote.text.toString()) {
                ToastUtils.showLongToast(mContext, R.string.Please_enter_the_transfer_amount)
                return@setOnClickListener
            }

            if (java.lang.Double.parseDouble(edtTransferCountVote.text.toString()) == 0.0) {
                ToastUtils.showLongToast(mContext, R.string.zhuanzhangbunengwei0)
                return@setOnClickListener

            }
            if (java.lang.Double.parseDouble(edtTransferCountVote.text.toString()) == 0.0) {
                ToastUtils.showLongToast(mContext, R.string.zhuanzhangbunengwei0)
                return@setOnClickListener

            }
            if (edtTransferCountVote.text.toString().toDouble() > fnBalance) {
                ToastUtils.showToast(mContext, R.string.yuebuzu)
                return@setOnClickListener
            }
            sendVote()

        }

        btn_what.setOnClickListener {
            val intent = Intent(this, WhatVoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun sendVote(){
        inputPwdDialog.show()
        inputPwdDialog.setDeleteAlertVisibility(false)
        inputPwdDialog.setOnInputDialogButtonClickListener(object : InputPwdDialog.OnInputDialogButtonClickListener {
            override fun onCancel() {
                inputPwdDialog.dismiss()
            }

            override fun onConfirm(pwd: String) {
                inputPwdDialog.dismiss()
                if (TextUtils.equals(WalletDaoUtils.getCurrent().password, Md5Utils.md5(pwd))) {
                    ToastUtils.showToast(mContext, R.string.mimazhengque)
                    showDialog("")
                    Thread(Runnable {
                        val voteContent = VoteContent()
                        voteContent.from = WalletDaoUtils.getCurrent().address.substring(2).toLowerCase()
                        voteContent.to = String(Base58.decode(edtVoteAddress.text.toString().trim()))

                        val value = BigDecimal(edtTransferCountVote.text.toString())
                                .multiply(BigDecimal("1000000000"))
                                .toBigInteger().toLong()
                        voteContent.value = value
                        voteContent.timestamp = System.currentTimeMillis() / 1000
                        if(DateUtil.isOneDay()) {
                            voteContent.expired = 241920
                        }else if(DateUtil.isMayOneDayUntilSixDay(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime())){
                            voteContent.expired = 190080
                        } else{
                            voteContent.expired = DateUtil.getExpiredTime(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime()) / 10000
                        }
                        voteContent.txid = TxIdUtils.CreateTxIdTranVoteContent(voteContent)
                        val privateKey = ETHWalletUtils.derivePrivateKey(WalletDaoUtils.getCurrent().id, pwd)
                        if (!TextUtils.isEmpty(privateKey)) {
                            val sign = SignatureUtils.signMsg(TxIdUtils.CreateTxIdTranVoteContentTe(voteContent), privateKey)
                            voteContent.sign = sign

                            voteContent.from = Base58.encode(voteContent.from.toByteArray())
                            voteContent.to = Base58.encode(voteContent.to.toByteArray())

                            voteContent.content = edtVoteContentContent.text.toString().trim()
                            val logInterceptor = HttpLoggingInterceptor()
                            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                            val client = OkHttpClient.Builder()
                                    .connectTimeout(15, TimeUnit.SECONDS)
                                    .addInterceptor(logInterceptor).build()
                            val content = Gson().toJson(voteContent)
//                            val url = "http://192.168.1.128:8080" + "/sendProxyVote"
//                            val url = "http://154.221.16.197:8088" + "/sendProxyVote"
                            val url = BuildConfig.APIHOST + "/sendProxyVotes"
                            val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content)
                            val postRequest = Request.Builder().url(url)
                                    .post(requestBody).build()
                            try {
                                val response = client.newCall(postRequest).execute()
                                val responseData = response.body()!!.string()
                                val baseResponse = Gson().fromJson(responseData, ReceiptDetailResponse::class.java)
                                if (baseResponse.code == 200) {
                                    runOnUiThread {
                                        hideDialog()
                                        ToastUtils.showToast(mContext, R.string.apply_for_voting_submit1)
                                        val intent = Intent(mContext, PayResultActivity::class.java)
                                        intent.putExtra("type", 3)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else if (baseResponse.code == 991) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.voting_for_this_month_has_ended_Please_start_voting_on_the_1st_of_next_month)
                                        hideDialog()
                                    }
                                } else if (baseResponse.code == 994) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.you_have_voted_for_the_other_packaging_node_address)
                                        hideDialog()
                                    }
                                } else if (baseResponse.code == 995) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.your_application_for_packaging_node_qualification_has_not_been_reviewed_please_review_and_then_vote)
                                        hideDialog()
                                    }
                                }
                                else if (baseResponse.code == 996) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.you_have_already_voted_you_cannot_apply_for_the_packaging_node)
                                        hideDialog()
                                    }
                                }else if (baseResponse.code == 997) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.apply_for_packaging_node_must_mortgage_1_FN)
                                        hideDialog()
                                    }
                                } else if (baseResponse.code == 998) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.this_address_is_super_node_address_cannot_participate_in_the_packaging_node_vote)
                                        hideDialog()
                                    }
                                }
                                else if (baseResponse.code == 993) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.your_address_is_the_super_node_address_you_cannot_participate_in_the_packaging_node_vote)
                                        hideDialog()
                                    }
                                }else if (baseResponse.code == 999) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.voting_address_has_not_applied_for_the_qualification_of_packaging_node_please_confirm_and_vote_again)
                                        hideDialog()
                                    }
                                }  else if (baseResponse.code == 202) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.handle_clicked)
                                        hideDialog()
                                    }
                                } else if (baseResponse.code == 980) {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.vote_address_is_not_exist)
                                        hideDialog()
                                    }
                                } else {
                                    runOnUiThread {
                                        ToastUtils.showLongToast(mContext, R.string.failed_vote_submit)
                                        hideDialog()
                                    }
                                }
                            } catch (e: Throwable) {
                                e.printStackTrace()
                                runOnUiThread {
                                    ToastUtils.showToast(mContext, R.string.network_error)
                                    hideDialog()
                                }
                            }

                        } else {
                            runOnUiThread {
                                ToastUtils.showToast(mContext, R.string.network_error)
                                hideDialog()
                            }
                        }
                    }).start()
                } else {
                    ToastUtils.showToast(mContext, R.string.wallet_detail_wrong_pwd)
                }
            }
        })
    }


    override fun initDatas() {
      loadBalance()
    }

    override fun configViews() {
    }

    private fun loadBalance() {

        Thread(Runnable {
            val client = OkHttpClient().newBuilder().connectTimeout(15, TimeUnit.SECONDS).build()
            val addBase58 = Base58.encode(WalletDaoUtils.getCurrent().address.toLowerCase().substring(2, WalletDaoUtils.getCurrent().address.length).toByteArray())
//            val url = "http://192.168.1.128:8080"  + "/blockByFrom?addars=$addBase58"
//            val url = "http://154.221.16.197:8088"  + "/blockByFrom?addars=$addBase58"
            val url = BuildConfig.APIHOST  + "/blockByFrom?addars=$addBase58"
            val request = Request.Builder().url(url).get().build()
            try {
                val response = client.newCall(request).execute()
                val rStr = response.body()!!.string()
                LogUtils.e(rStr)
                val fnBalanceBean = Gson().fromJson(rStr, FnBalanceBean::class.java)
                if (fnBalanceBean.status == 200) {
                    if (fnBalanceBean.data != null) {
                        val balance = fnBalanceBean.data.balance
                        fnBalance = if (TextUtils.isEmpty(balance)) {
                            0.0
                        } else {
                            val wei = BigInteger(balance)
                            wei.toDouble() / BigInteger("1000000000").toDouble()
                        }
                    }
                }
                runOnUiThread { fnCount.text = String.format(getString(R.string.available_balance) + "  %.04f FN", fnBalance) }
            } catch (e: Throwable) {
                e.printStackTrace()
                fnBalance = 0.0
                runOnUiThread { fnCount.text = String.format(getString(R.string.available_balance) + "  %.04f FN", fnBalance) }
            }
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                val scanResult = data.getStringExtra("scan_result")
                ToastUtils.showLongToast(mContext, scanResult)
                edtVoteAddress.setText(scanResult)
            }
        }
    }
}