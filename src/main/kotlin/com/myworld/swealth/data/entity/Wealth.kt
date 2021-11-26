package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "WEALTH")
class Wealth(
    @javax.persistence.Id
    var id: String? = null,
    @JsonIgnore
    var ukey: String? = null,

    var code: String? = null,
    var codeRegister: String? = null,

    var name: String? = null,
    var bankName: String? = null,
    var bankLevel: String? = null,

    var risk: Int? = null,
    var term: Int? = null,
    var termLooped: String? = null,
    var currency: String? = null,

    var redeemType: String? = null,
    var fixedType: String? = null,
    var promiseType: String? = null,
    var rateType: String? = null,

    var rateMin: Float? = null,
    var rateMax: Float? = null,
    var rateNetvalue: String? = null,

    var amountBuyMin: Long? = null,

    var fileType: String? = null,

    @JsonIgnore
    var status: String? = null,
    @JsonIgnore
    var createTime: String? = null
    ) : Serializable, Comparable<Wealth> {


    /**
     * 设置为升序排列，即小的在前，大的在后
     */
    override fun compareTo(other: Wealth): Int {
        return if (rateMax!! >= other.rateMax!!) {
            rateMin!!.compareTo(other.rateMin!!)
        } else {
            -1
        }
    }
}
