package com.myworld.swealth.web.controller

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.web.service.JobService
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/job")
class JobController {
    private val log : Logger = LogManager.getRootLogger()

    @Autowired
    private lateinit var jobService: JobService

    @GetMapping("/detail/{id}")
    fun jobDetail(@PathVariable id: String): ApiResult<*>? {
        return jobService.jobDetail(id)
    }

    @GetMapping("/jobs")
    fun jobs(@RequestParam bankName: String?, @RequestParam pageSize: Int, @RequestParam pageIndex: Int): ApiResult<*>? {
        return jobService.jobs(bankName, pageSize, pageIndex)
    }

}
