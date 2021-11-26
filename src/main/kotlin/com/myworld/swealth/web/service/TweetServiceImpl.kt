package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.common.ResultUtil
import com.myworld.swealth.common.StringUtil
import com.myworld.swealth.common.UserContextHolder
import com.myworld.swealth.data.entity.Tweet
import com.myworld.swealth.data.entity.TweetComment
import com.myworld.swealth.data.entity.TweetPhoto
import com.myworld.swealth.data.repository.TweetCommentRepository
import com.myworld.swealth.data.repository.TweetPhotoRepository
import com.myworld.swealth.data.repository.TweetRepository
import com.myworld.swealth.data.repository.UserProfileRepository
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.jdbc.core.BeanPropertyRowMapper
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*
import javax.servlet.http.HttpServletResponse
import kotlin.collections.ArrayList


@Service
class TweetServiceImpl : TweetService {

    private val log = LogManager.getRootLogger()
    private val MYTWEET_PREFIX = "MYWEET"
    private val TWEET_COMMENT_PREFIX = "TCOMNT"
    private val TWEET_PHOTO_PREFIX = "TPHOTO"

    @Autowired
    private lateinit var tweetRepository: TweetRepository
    @Autowired
    private lateinit var tweetCommentRepository: TweetCommentRepository
    @Autowired
    private lateinit var tweetPhotoRepository: TweetPhotoRepository
    @Autowired
    private lateinit var userProfileRepository: UserProfileRepository
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    @Autowired
    private lateinit var namedParameterJdbcTemplate: NamedParameterJdbcTemplate


    override fun showSocialList(pageSize: Int, pageIndex: Int, userWid: String?): ApiResult<*>? {
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        val totalNum: Long
        val tweets: MutableList<Tweet>
        if (userWid.isNullOrEmpty()) {
            val tweetPaged = tweetRepository.findAll(pageable) ?: return ResultUtil.failure(-2, "没有数据")
            tweets = tweetPaged.content;
            totalNum = tweetPaged.totalElements
        } else {
            val tweetPaged = tweetRepository.findByUserWid(userWid, pageable) ?: return ResultUtil.failure(-2, "没有数据")
            tweets = tweetPaged.content;
            totalNum = tweetPaged.totalElements
        }

        for (tweet: Tweet in tweets) {
            val viewCount = tweet.viewCount ?: 0
            tweet.viewCount = viewCount + 1
            tweetRepository.save(tweet)
        }
        return ResultUtil.success(num = totalNum, data = tweets)
    }

    override fun showSocialListWatch(pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "请先设置个人信息")
        }
        val userProfile = userProfileOption.get()
        var watchUserWids = userProfile.watchUserWids!!
        watchUserWids = watchUserWids.substring(0, watchUserWids.length - 1)
        val watchList = watchUserWids.split(',')
        val watchListNeed : MutableList<String> = ArrayList()
        for (one: String in watchList) {
            watchListNeed.add("'$one'")
        }
        val watchListString = watchListNeed.joinToString(",")

        val sql = "SELECT count(*) FROM tweet WHERE user_wid IN ($watchListString)"
        if (pageIndex == 0) {
            val totalCount = jdbcTemplate.queryForObject(sql, Integer::class.java) as Int
            log.info("=========================== 总共记录有，{}", totalCount)
            if (totalCount == 0) {
                return ResultUtil.failure(-2, "没有关注信息")
            }
        }
        val start = pageIndex * pageSize
        var sqlNeed = sql.replace("count(*)", "*")
        sqlNeed = "$sqlNeed ORDER BY create_time DESC limit $start,$pageSize"
        log.info("sql语句是：{}", sqlNeed)
        val tweets: MutableList<Tweet> = jdbcTemplate.query(sqlNeed, BeanPropertyRowMapper(Tweet::class.java))
        return ResultUtil.success(data = tweets)
    }

    fun queryShowSocialList(pageSize: Int, pageIndex: Int, userWid: String?): ApiResult<*>? {
        val start = pageIndex * pageSize
        val sqlCount = "SELECT count(*) FROM tweet"
        val totalCount = jdbcTemplate.queryForObject(sqlCount, Integer::class.java)
        log.info("=========================== 总共记录有，{}", totalCount)
        val sql = "SELECT * FROM tweet ORDER BY create_time DESC limit $start,$pageSize"
        log.info("sql语句是：{}", sql)
        val tweets: MutableList<Tweet> = jdbcTemplate.query(sql, BeanPropertyRowMapper(Tweet::class.java))

        return ResultUtil.success(data = tweets)
    }

    fun queryShowSocialList_exampleOne(pageSize: Int, pageIndex: Int, userWid: String?): ApiResult<*>? {
        val start = pageIndex * pageSize
        val sqlCount = "SELECT count(*) FROM tweet"
        val totalCount = jdbcTemplate.queryForObject(sqlCount, Integer::class.java)
        log.info("=========================== 总共记录有，{}", totalCount)
        val sql = "SELECT id AS id, nickname AS nickname FROM tweet ORDER BY create_time DESC limit $start,$pageSize"
        log.info("sql语句是：{}", sql)
        val tweets = namedParameterJdbcTemplate.jdbcTemplate.query(sql.trimMargin()) {
            rs, _ -> Tweet(id = rs.getString(1), nickname = rs.getString(2))
        }
        return ResultUtil.success(data = tweets)
    }

    fun queryShowSocialList_exampleTwo(pageSize: Int, pageIndex: Int, userWid: String?): ApiResult<*>? {
        val start = pageIndex * pageSize
        val sqlCount = "SELECT count(*) FROM tweet"
        val totalCount = jdbcTemplate.queryForObject(sqlCount, Integer::class.java)
        log.info("=========================== 总共记录有，{}", totalCount)
        val sql = "SELECT * FROM tweet ORDER BY create_time DESC limit $start,$pageSize"
        log.info("sql语句是：{}", sql)
        val tweets: MutableList<Tweet> = jdbcTemplate.query(sql, BeanPropertyRowMapper(Tweet::class.java))

        return ResultUtil.success(data = tweets)
    }


    @Throws(IOException::class)
    override fun tweetPhoto(id: String, response: HttpServletResponse) {
        val file = tweetPhotoRepository.findById(id)
        if (file.isPresent) {
            IOUtils.copy(ByteArrayInputStream(file.get().fileByte), response.outputStream)
            response.contentType = "image/jpeg"
        }
    }

    override fun getSocialComments(tweetWid: String, tweetUserWid: String, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfile = userProfileRepository.existsByIdAndWatchUserWidsContaining(userWid, tweetUserWid)
        var msg = "false"
        if (userProfile) {
            msg = "true"
        }
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        val tweetCommentPaged = tweetCommentRepository.findByTweetWid(tweetWid, pageable) ?: return ResultUtil.failure(-2, "没有数据")
        return ResultUtil.success(msg = msg, num = tweetCommentPaged.totalElements, data = tweetCommentPaged.content)
    }


    override fun commentOnSocial(tweetWid: String, idAt: String, commenterWid: String, commentatWid: String, comment: String): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        if (userWid != commenterWid) {
            return ResultUtil.failure(-2, "权限不匹配")
        }
        // 找出评论者的个人资料
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "请更新个人资料")
        }
        val userProfile = userProfileOption.get()

        val tweetOption = tweetRepository.findById(tweetWid)
        if (tweetOption.isPresent) {
            val tweet = tweetOption.get()
            val count = tweet.commentCount ?: 0
            tweet.commentCount = count + 1
            tweetRepository.save(tweet)
            val wid = TWEET_COMMENT_PREFIX + '-' + commenterWid + '-' +Date().time
            val tweetComment = TweetComment(
                id = wid,
                tweetWid = tweetWid,
                idAt = idAt,
                comment = comment,
                commenterWid = commenterWid,
                commentatWid = commentatWid,
                nickname = userProfile.nickname,
                position = userProfile.position,
                company = userProfile.company
            )
            tweetCommentRepository.save(tweetComment)
            return ResultUtil.success(data = wid)
        } else {
            return ResultUtil.failure(-2, "没有该博客")
        }
    }

    override fun createShowSocial(anonymous: Int, content: String): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")


        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "请更新个人资料")
        }
        val userProfile = userProfileOption.get()

        val currentTime = Date().time
        val wid = MYTWEET_PREFIX + "-" + userWid + "-" + currentTime
        val tweet = Tweet(
            id = wid,
            anonymous = anonymous,
            content = content,
            userWid = userWid,
            nickname = userProfile.nickname,
            company = userProfile.company,
            position = userProfile.position,
            createTime = currentTime)
        tweetRepository.save(tweet)
        return ResultUtil.success(data = wid)
    }

    override fun uploadTweetPhotoBase64(blogIdDetail: String, base64s: Array<String>): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")

        val tweetOption = tweetRepository.findById(blogIdDetail)
        if (!tweetOption.isPresent) {
            return ResultUtil.failure(-2, "无此信息，不能上传照片")
        }
        val newNum = base64s.size
        val theNum = tweetPhotoRepository.countByTweetWid(blogIdDetail)
        if (theNum + newNum > 9) {
            return ResultUtil.failure(-2, "最多只能上传9张照片")
        }
        val tweet = tweetOption.get()
        var tweetPhotos = tweet.tweetPhotos
        for (base64 in base64s) {
            val photoWid = saveTweetPhoto(blogIdDetail, base64)
            tweetPhotos = "$tweetPhotos$photoWid,"
        }
        tweet.tweetPhotos = tweetPhotos
        tweetRepository.save(tweet)
        return ResultUtil.success()
    }

    override fun saveTweetPhoto(blogIdDetail: String, base64: String): String {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid
        val base64Bytes = StringUtil.base64ToBytes(base64)
        val size = base64Bytes.size
        val currentTime = Date().time
        val wid = TWEET_PHOTO_PREFIX + currentTime + ((Math.random() * 9 + 1) * 1000).toInt()
        val tweetPhoto = TweetPhoto(
            id = wid,
            fileName = "BASE64-$currentTime.jpg",
            extensionType = "image/jpeg",
            userWid = userWid,
            tweetWid = blogIdDetail,
            fileByte = base64Bytes,
            size = size.toLong(),
            createTime = currentTime
        )
        tweetPhotoRepository.save(tweetPhoto)
        return wid
    }

    override fun delShowSocial(id: String, userWid: String): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val theUserWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        if (userWid != theUserWid) {
            return ResultUtil.failure(-2, "权限不匹配")
        }
        val tweetOption = tweetRepository.findById(id)
        if (!tweetOption.isPresent) {
            return ResultUtil.failure(-2, "不存在该微博")
        }
        val tweetUserWid = tweetOption.get().userWid
        if (userWid != tweetUserWid) {
            return ResultUtil.failure(-2, "无权删除别人的微博")
        }
        tweetCommentRepository.deleteAllByTweetWid(id)
        tweetPhotoRepository.deleteAllByTweetWid(id)
        tweetRepository.deleteById(id)
        return ResultUtil.success()
    }
}
