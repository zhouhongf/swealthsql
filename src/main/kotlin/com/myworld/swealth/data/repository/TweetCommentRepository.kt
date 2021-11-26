package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.TweetComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface TweetCommentRepository : JpaRepository<TweetComment, String> {

    fun findByTweetWid(tweetWid: String, pageable: Pageable): Page<TweetComment>?
    fun deleteAllByTweetWid(tweetWid: String)
}
