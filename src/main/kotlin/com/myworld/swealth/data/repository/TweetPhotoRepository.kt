package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.TweetPhoto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface TweetPhotoRepository : JpaRepository<TweetPhoto, String>{

    fun countByTweetWid(tweetWid: String): Int
    fun deleteAllByTweetWid(tweetWid: String)
}
