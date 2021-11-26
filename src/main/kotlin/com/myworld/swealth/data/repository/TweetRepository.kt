package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.Tweet
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface TweetRepository : JpaRepository<Tweet, String> {

    fun findByUserWid(userWid: String, pageable: Pageable): Page<Tweet>?
}
