package com.myworld.swealth.data.entity

import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tweet_comment")
class TweetComment(
    @javax.persistence.Id
    var id: String? = null,
    var idAt: String? = null,
    var tweetWid: String? = null,

    @Column(name = "comment", columnDefinition = "MEDIUMTEXT")
    var comment: String? = null,
    var commenterWid: String? = null,
    var nickname: String? = null,
    var position: String? = null,
    var company: String? = null,

    var commentatWid: String? = null,

    var likeCount: Int? = 0,
    var status: String? = "undo",
    var createTime: Long = Date().time
) : Serializable
