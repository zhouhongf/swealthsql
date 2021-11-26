package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.data.entity.Wealth
import com.myworld.swealth.data.model.WealthOutline
import java.io.IOException
import javax.servlet.http.HttpServletResponse

interface WealthService {
    fun getWealthInfo(id: String): ApiResult<Any?>
    @Throws(IOException::class)
    fun getWealthManual(id: String, response: HttpServletResponse)

    fun wealthsToWealthOutlines(wealths: List<Wealth>): MutableList<WealthOutline>

    fun textAndWealth(bankName: String): ApiResult<Any?>
    fun wealthMore(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>?

    fun overallOutline(): ApiResult<*>
}
