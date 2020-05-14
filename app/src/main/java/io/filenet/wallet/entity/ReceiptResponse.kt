package io.filenet.wallet.entity

class ReceiptResponse {
    var code: Int = 0
    var `object`: DataBean? = null

    class DataBean {
        var list: MutableList<ReceiptAndProxy> = arrayListOf()
    }

}