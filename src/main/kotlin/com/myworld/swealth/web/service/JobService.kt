package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.data.entity.Job
import com.myworld.swealth.data.model.JobOutline

interface JobService {

    fun jobDetail(id: String): ApiResult<*>?
    fun jobs(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>?
    fun jobsToJobOutlines(jobs: List<Job>): MutableList<JobOutline>
}
