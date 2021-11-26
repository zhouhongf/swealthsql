package com.myworld.swealth.web.controller

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.web.service.UserService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/user")
class UserController {

    private val log : Logger = LogManager.getRootLogger()
    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/getUserInfo")
    fun getUserInfo(): ApiResult<*>?  {
        return userService.getUserInfo()
    }

    @PostMapping("/setUserInfo")
    fun setUserInfo(@RequestBody userInfoStr: String): ApiResult<*>?  {
        return userService.setUserInfo(userInfoStr)
    }

    @PostMapping("/setNickname")
    fun setNickname(@RequestParam nickname: String, @RequestBody userIntro: String?): ApiResult<*>?  {
        return userService.setNickname(nickname, userIntro)
    }

    @GetMapping("/getNickname")
    fun getNickname(): ApiResult<*>? {
        return userService.getNickname()
    }

    @GetMapping("/getUserOutline")
    fun getUserOutline(): ApiResult<*>? {
        return userService.getUserOutline()
    }

    @GetMapping("/getWatchUsers")
    fun getWatchUsers() : ApiResult<*>? {
        return userService.getWatchUsers()
    }

    @GetMapping("/addUserWatch")
    fun addUserWatch(@RequestParam userWidWatch: String) : ApiResult<*>? {
        return userService.addUserWatch(userWidWatch)
    }

    @DeleteMapping("/delUserWatch")
    fun delUserWatch(@RequestParam userWidWatch: String) : ApiResult<*>? {
        return userService.delUserWatch(userWidWatch)
    }
}
