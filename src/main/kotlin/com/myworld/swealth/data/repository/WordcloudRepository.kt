package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.Wordcloud
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface WordcloudRepository : JpaRepository<Wordcloud, String>
