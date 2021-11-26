package com.myworld.swealth.web.controller

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.web.service.TweetService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.IOException
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/tweet")
class TweetController {

    private val log : Logger = LogManager.getRootLogger()
    companion object {
        private const val indexName = "tweet"
    }

    @Autowired
    private lateinit var tweetService: TweetService

    @GetMapping("/showSocialList")
    fun showSocialList(@RequestParam pageSize: Int, @RequestParam pageIndex: Int, @RequestParam userWid: String?): ApiResult<*>? {
        return tweetService.showSocialList(pageSize, pageIndex, userWid)
        // return tweetService.queryShowSocialList(pageSize, pageIndex, userWid)
    }

    @GetMapping("/showSocialListWatch")
    fun showSocialListWatch(@RequestParam pageSize: Int, @RequestParam pageIndex: Int): ApiResult<*>? {
        return tweetService.showSocialListWatch(pageSize, pageIndex)
    }

    @GetMapping("/tweetPhoto/{id}")
    @Throws(IOException::class)
    fun tweetPhoto(@PathVariable id: String, response: HttpServletResponse) {
        tweetService.tweetPhoto(id, response)
    }

    @GetMapping("/getSocialComments")
    fun getSocialComments(@RequestParam tweetWid: String, @RequestParam tweetUserWid: String, @RequestParam pageSize: Int, @RequestParam pageIndex: Int): ApiResult<*>? {
        return tweetService.getSocialComments(tweetWid, tweetUserWid, pageSize, pageIndex)
    }

    @PostMapping("/commentOnSocial")
    fun commentOnSocial(
        @RequestParam tweetWid: String,
        @RequestParam idAt: String,
        @RequestParam commenterWid: String,
        @RequestParam commentatWid: String,
        @RequestBody comment: String
    ): ApiResult<*>? {
        return tweetService.commentOnSocial(tweetWid, idAt, commenterWid, commentatWid, comment)
    }

    @PostMapping("/createShowSocial")
    fun createShowSocial(@RequestParam anonymous: Int, @RequestBody content: String): ApiResult<*>? {
        return tweetService.createShowSocial(anonymous, content)
    }

    @PostMapping("/uploadTweetPhotoBase64")
    fun uploadTweetPhotoBase64(@RequestParam blogIdDetail: String, @RequestBody base64s: Array<String>): ApiResult<*>? {
        return tweetService.uploadTweetPhotoBase64(blogIdDetail, base64s)
    }

    @DeleteMapping("/delShowSocial")
    fun delShowSocial(@RequestParam id: String, @RequestParam userWid: String): ApiResult<*>? {
        return tweetService.delShowSocial(id, userWid)
    }
}
