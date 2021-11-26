package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tweet")
class Tweet(
    @javax.persistence.Id
    var id: String? = null,
    var anonymous: Int? = null,
    var userWid: String? = null,
    var nickname: String? = null,
    var position: String? = null,
    var company: String? = null,

    @Column(name = "content", columnDefinition = "LONGTEXT COMMENT '大文本格式'")
    var content: String? = "",
    var tweetPhotos: String? = "",
    var tweetComments: String? = "",

    var canShare: Boolean? = true,
    var shareCount: Int? = 0,

    var favorCount: Int? = 0,
    var likeCount: Int? = 0,
    var viewCount: Int? = 0,
    var commentCount: Int? = 0,

    @JsonIgnore
    var status: String? = "undo",
    @JsonIgnore
    var createTime: Long = Date().time,
    var updateTime: Long = Date().time
) : Serializable, Comparable<Tweet> {

    /**
     * 设置为升序排列，即小的在前，大的在后
     */
    override fun compareTo(other: Tweet): Int {
        return updateTime.compareTo(other.updateTime)
    }
}
