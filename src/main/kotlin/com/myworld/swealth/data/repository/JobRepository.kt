package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface JobRepository : JpaRepository<Job, String> {

    fun findByBankName(bankName: String, pageable: Pageable): Page<Job>?
}
