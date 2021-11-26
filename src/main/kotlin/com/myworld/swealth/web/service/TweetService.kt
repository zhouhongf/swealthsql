package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import java.io.IOException
import javax.servlet.http.HttpServletResponse

interface TweetService {

    fun showSocialList(pageSize: Int = 10, pageIndex: Int = 0, userWid: String?): ApiResult<*>?
    fun showSocialListWatch(pageSize: Int = 10, pageIndex: Int = 0): ApiResult<*>?

    @Throws(IOException::class)
    fun tweetPhoto(id: String, response: HttpServletResponse)
    fun getSocialComments(tweetWid: String, tweetUserWid: String, pageSize: Int, pageIndex: Int): ApiResult<*>?
    fun commentOnSocial(tweetWid: String, idAt: String, commenterWid: String, commentatWid: String, comment: String): ApiResult<*>?
    fun createShowSocial(anonymous: Int, content: String): ApiResult<*>?

    fun uploadTweetPhotoBase64(blogIdDetail: String, base64s: Array<String>): ApiResult<*>?
    fun saveTweetPhoto(blogIdDetail: String, base64: String): String

    fun delShowSocial(id: String, userWid: String): ApiResult<*>?
}
