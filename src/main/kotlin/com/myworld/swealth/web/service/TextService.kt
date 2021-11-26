package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.data.entity.Text
import com.myworld.swealth.data.model.TextOutline
import java.io.IOException
import javax.servlet.http.HttpServletResponse


interface TextService {

    fun detail(id: String): ApiResult<Any?>
    @Throws(IOException::class)
    fun wordcloud(id: String, response: HttpServletResponse)

    fun textMore(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>?
    fun textsToTextOutlines(texts: List<Text>): MutableList<TextOutline>

    fun textCategory(bankName: String?, keyone: String, keytwo: String?, pageSize: Int, pageIndex: Int): ApiResult<*>?
    fun textFinance(bankName: String?, keyone: String, keytwo: String, pageSize: Int, pageIndex: Int): ApiResult<*>?
}
