package com.myworld.swealth.web.service

import com.alibaba.fastjson.JSONObject
import com.fasterxml.jackson.databind.ObjectMapper
import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.common.ResultUtil
import com.myworld.swealth.common.UserContextHolder
import com.myworld.swealth.data.entity.UserFavor
import com.myworld.swealth.data.entity.UserProfile
import com.myworld.swealth.data.entity.Wealth
import com.myworld.swealth.data.model.UserOutline
import com.myworld.swealth.data.repository.UserFavorRepository
import com.myworld.swealth.data.repository.UserProfileRepository
import com.myworld.swealth.data.repository.WealthRepository
import com.myworld.swealth.security.MyUserKeyService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.ArrayList


@Service
class UserServiceImpl : UserService {

    private val log = LogManager.getRootLogger()

    @Autowired
    private lateinit var userProfileRepository: UserProfileRepository
    @Autowired
    private lateinit var userFavorRepository: UserFavorRepository
    @Autowired
    private lateinit var wealthRepository: WealthRepository

    override fun getUserInfo(): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")

        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }

        val user = userProfileOption.get()
        val objectMapper = ObjectMapper()
        val userInfoStr: String = objectMapper.writeValueAsString(user)
        val userInfoStrEncode: String = MyUserKeyService.aesEncrypt(userInfoStr)
        return ResultUtil.success(data = userInfoStrEncode)
    }

    override fun setUserInfo(userInfoStr: String) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")

        val userInfoString = userInfoStr.replace(" ", "+")
        val userInfoStrDecoded = MyUserKeyService.aesDecrypt(userInfoString)
        val jsonObject = JSONObject.parseObject(userInfoStrDecoded)
        val userInfo = JSONObject.toJavaObject(jsonObject, UserProfile::class.java)

        val userProfileOption = userProfileRepository.findById(userWid)
        val userProfile = if (userProfileOption.isPresent) {
            userProfileOption.get()
        } else {
            UserProfile(id = userWid)
        }
        if (userProfile.watchUserWids.isNullOrEmpty()) {
            userProfile.watchUserWids = "$userWid,"
        }
        userProfile.gender = userInfo.gender
        userProfile.intro = userInfo.intro
        userProfile.realname = userInfo.realname
        userProfile.email = userInfo.email
        userProfile.idtype = userInfo.idtype
        userProfile.position = userInfo.position
        userProfile.industry = userInfo.industry
        userProfile.company = userInfo.company
        userProfile.placebelong = userInfo.placebelong
        userProfile.address = userInfo.address
        userProfile.offer = userInfo.offer
        userProfile.updateTime = Date().time
        userProfileRepository.save(userProfile)
        return ResultUtil.success()
    }

    override fun setNickname(nickname: String, userIntro: String?) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        val userProfile = if (userProfileOption.isPresent) {
            userProfileOption.get()
        } else {
            UserProfile(id = userWid)
        }
        userProfile.nickname = nickname
        userProfile.intro = userIntro
        userProfile.updateTime = Date().time
        userProfileRepository.save(userProfile)
        return ResultUtil.success()
    }

    override fun getNickname(): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }
        val nickname = userProfileOption.get().nickname
        return ResultUtil.success(data = nickname)
    }

    override fun getUserOutline(): ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }
        val userProfile = userProfileOption.get()
        val userOutline = UserOutline(
            gender = userProfile.gender,
            intro = userProfile.intro,
            nickname = userProfile.nickname,
            position = userProfile.position,
            industry = userProfile.industry,
            company = userProfile.company
        )
        return ResultUtil.success(data = userOutline)
    }


    override fun getWatchUsers() : ApiResult<*>? {
        log.info(" ============================= 开始执行getWatchUsers方法")
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }

        val userProfile = userProfileOption.get()
        var watchUserWids = userProfile.watchUserWids!!
        watchUserWids = watchUserWids.substring(0, watchUserWids.length - 1)
        val watchList = watchUserWids.split(',')
        if (watchList.isEmpty()) {
            return ResultUtil.success()
        }
        val watchUsers: MutableList<UserOutline> = ArrayList()
        for (wid in watchList) {
            val userProfileWatchOption = userProfileRepository.findById(wid)
            if (userProfileWatchOption.isPresent) {
                val userProfileWatch = userProfileWatchOption.get()
                val userOutline = UserOutline(
                    id = userProfileWatch.id,
                    gender = userProfileWatch.gender,
                    intro = userProfileWatch.intro,
                    nickname = userProfileWatch.nickname,
                    position = userProfileWatch.position,
                    industry = userProfileWatch.industry,
                    company = userProfileWatch.company
                )
                watchUsers.add(userOutline)
            }
        }
        return ResultUtil.success(data = watchUsers)
    }

    override fun addUserWatch(userWidWatch: String) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }

        val userProfile = userProfileOption.get()
        var watchUserWids = userProfile.watchUserWids!!
        watchUserWids = watchUserWids.substring(0, watchUserWids.length - 1)
        val watchList = watchUserWids.split(',')
        if (watchList.isEmpty()) {
            userProfile.watchUserWids = "$userWidWatch,"
            userProfileRepository.save(userProfile)
            return ResultUtil.success()
        }
        if (watchList.contains(userWidWatch)) {
            return ResultUtil.success(msg = "已关注过了")
        }
        userProfile.watchUserWids = "$watchUserWids,$userWidWatch,"
        userProfileRepository.save(userProfile)

        // 将添加的关注用户的信息返回给前端
        // val userProfileWatchOption = userProfileRepository.findById(userWidWatch)
        // if (userProfileWatchOption.isPresent) {
        //    val userProfileWatch = userProfileWatchOption.get()
        //    val userOutline = UserOutline(
        //         id = userProfileWatch.id,
        //         gender = userProfileWatch.gender,
        //         intro = userProfileWatch.intro,
        //         nickname = userProfileWatch.nickname,
        //         position = userProfileWatch.position,
        //         industry = userProfileWatch.industry,
        //         company = userProfileWatch.company
        //     )
        //     return ResultUtil.success(data = userOutline)
        // }
        return ResultUtil.success()
    }

    override fun delUserWatch(userWidWatch: String) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userProfileOption = userProfileRepository.findById(userWid)
        if (!userProfileOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }
        if (userWid == userWidWatch) {
            return ResultUtil.failure(-2, "本人用户无须关注或删除")
        }

        val userProfile = userProfileOption.get()
        val watchUserWids = userProfile.watchUserWids!!
        val indexNum = watchUserWids.indexOf(userWidWatch)
        if (indexNum == -1) {
            return ResultUtil.success(msg = "没有关注")
        }
        log.info("=============== 替换之前的watchUserWids是：{}", watchUserWids)
        val watchUserWidsNew = watchUserWids.replace("$userWidWatch,", "")
        log.info("=============== 替换之后的watchUserWids是：{}", watchUserWidsNew)
        userProfile.watchUserWids = watchUserWidsNew
        userProfileRepository.save(userProfile)
        return ResultUtil.success()
    }


    override fun getFavorWealths() : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userFavorOption = userFavorRepository.findById(userWid)
        if (!userFavorOption.isPresent) {
            return ResultUtil.failure(-2, "没有该用户信息")
        }

        val userFavor = userFavorOption.get()
        var favorWealths = userFavor.favorWealths!!
        favorWealths = favorWealths.substring(0, favorWealths.length - 1)
        val favorList = favorWealths.split(',')
        if (favorList.isEmpty()) {
            return ResultUtil.success()
        }
        val wealthList: MutableList<Wealth> = ArrayList()
        for (wid in favorList) {
            val wealthOption = wealthRepository.findById(wid)
            if (wealthOption.isPresent) {
                val wealth = wealthOption.get()
                wealthList.add(wealth)
            }
        }
        return ResultUtil.success(data = wealthList)
    }

    override fun addFavorWealth(wealthWid: String) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userFavorOption = userFavorRepository.findById(userWid)
        if (!userFavorOption.isPresent) {
            val userFavor = UserFavor(id = userWid, favorWealths = "$wealthWid,")
            userFavorRepository.save(userFavor)
        } else {
            val userFavor = userFavorOption.get()
            var favorWealths = userFavor.favorWealths!!
            favorWealths = favorWealths.substring(0, favorWealths.length - 1)
            val favorList = favorWealths.split(',')
            if (favorList.isEmpty()) {
                userFavor.favorWealths = "$wealthWid,"
            } else {
                userFavor.favorWealths = "$favorWealths,$wealthWid,"
            }
            userFavorRepository.save(userFavor)
        }
        return ResultUtil.success()
    }


    override fun delFavorWealth(wealthWid: String) : ApiResult<*>? {
        val simpleUser = UserContextHolder.getUserContext()
        val userWid = simpleUser.wid ?: return ResultUtil.failure(-2, "没有权限")
        val userFavorOption = userFavorRepository.findById(userWid)
        if (!userFavorOption.isPresent) {
            return ResultUtil.success()
        }

        val userFavor = userFavorOption.get()
        val favorWealths = userFavor.favorWealths!!
        val indexNum = favorWealths.indexOf(wealthWid)
        if (indexNum == -1) {
            return ResultUtil.success()
        }
        val favorWealthsNew = favorWealths.replace("$wealthWid,", "")
        userFavor.favorWealths = favorWealthsNew
        userFavorRepository.save(userFavor)
        return ResultUtil.success()
    }
}
