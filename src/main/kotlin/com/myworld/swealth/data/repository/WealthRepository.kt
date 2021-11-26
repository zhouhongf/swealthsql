package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.Wealth
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface WealthRepository : JpaRepository<Wealth, String> {
    fun findByBankName(bankName: String, pageable: Pageable): Page<Wealth>
    fun findFirst5ByBankName(bankName: String, sort: Sort): List<Wealth>
}
