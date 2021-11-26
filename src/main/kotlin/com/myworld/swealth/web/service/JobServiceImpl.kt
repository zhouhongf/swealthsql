package com.myworld.swealth.web.service

import com.myworld.swealth.common.ApiResult
import com.myworld.swealth.common.ResultUtil
import com.myworld.swealth.data.entity.Job
import com.myworld.swealth.data.entity.Text
import com.myworld.swealth.data.model.JobOutline
import com.myworld.swealth.data.model.TextOutline
import com.myworld.swealth.data.repository.JobRepository
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class JobServiceImpl : JobService {

    private val log = LogManager.getRootLogger()

    @Autowired
    private lateinit var jobRepository: JobRepository

    override fun jobDetail(id: String): ApiResult<*>? {
        val job = jobRepository.findById(id)
        return if (job.isPresent) {
            ResultUtil.success(data = job.get())
        } else {
            ResultUtil.failure(code = -2, msg = "没有找到数据")
        }
    }

    override fun jobs(bankName: String?, pageSize: Int, pageIndex: Int): ApiResult<*>? {
        val pageable: Pageable = PageRequest.of(pageIndex, pageSize, Sort.Direction.DESC, "createTime")
        val jobsPaged = if (bankName.isNullOrEmpty()) {
            jobRepository.findAll(pageable)
        } else {
            jobRepository.findByBankName(bankName = bankName, pageable = pageable)
        } ?: return ResultUtil.failure(-2, "没有数据")
        val jobsList = jobsToJobOutlines(jobsPaged.content)
        return ResultUtil.success(num = jobsPaged.totalElements, data = jobsList)
    }

    override fun jobsToJobOutlines(jobs: List<Job>): MutableList<JobOutline> {
        val jobOutlineList: MutableList<JobOutline> = ArrayList()
        for (job in jobs) {
            val jobOutline = JobOutline(id = job.id, name = job.name, bankName = job.bankName, branchName = job.branchName, department = job.department, place = job.place, datePublish = job.datePublish, dateClose = job.dateClose)
            jobOutlineList.add(jobOutline)
        }
        return jobOutlineList
    }
}
