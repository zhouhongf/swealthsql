package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult

interface UserService {
    fun getUserInfo(): ApiResult<*>?
    fun setUserInfo(userInfoStr: String) : ApiResult<*>?
    fun setNickname(nickname: String, userIntro: String?) : ApiResult<*>?
    fun getNickname(): ApiResult<*>?
    fun getUserOutline(): ApiResult<*>?

    fun getWatchUsers() : ApiResult<*>?
    fun addUserWatch(userWidWatch: String) : ApiResult<*>?
    fun delUserWatch(userWidWatch: String) : ApiResult<*>?

    fun getFavorWealths() : ApiResult<*>?
    fun addFavorWealth(wealthWid: String) : ApiResult<*>?
    fun delFavorWealth(wealthWid: String) : ApiResult<*>?
}
