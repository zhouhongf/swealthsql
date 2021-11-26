package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.Text
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface TextRepository : JpaRepository<Text, String> {

    fun findByTypeMain(typeMain: String, pageable: Pageable): Page<Text>
    fun findByBankNameAndTypeMain(bankName: String, typeMain: String, pageable: Pageable): Page<Text>

    fun findByTypeMainAndTypeNextContaining(typeMain: String, typeNext: String, pageable: Pageable): Page<Text>
    fun findByBankNameAndTypeMainAndTypeNextContaining(bankName: String, typeMain: String, typeNext: String, pageable: Pageable): Page<Text>

    fun findByTypeMainAndTypeNextAndTypeOne(typeMain: String, typeNext: String, typeOne: String, pageable: Pageable): Page<Text>
    fun findByBankNameAndTypeMainAndTypeNextAndTypeOne(bankName: String, typeMain: String, typeNext: String, typeOne: String, pageable: Pageable): Page<Text>?
}
