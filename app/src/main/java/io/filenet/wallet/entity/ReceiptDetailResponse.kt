package io.filenet.wallet.entity

class ReceiptDetailResponse {

    var code: Int = 0
    var `object`: DataBean? = null

    class DataBean {
        var data: RxBean? = null


        class RxBean {
            var rxid: String? = null
            var height: Long? = null
        }
    }


}