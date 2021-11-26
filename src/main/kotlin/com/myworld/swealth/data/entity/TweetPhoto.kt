package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "tweet_photo")
class TweetPhoto(
    @javax.persistence.Id
    var id: String? = null,
    var userWid: String? = null,
    var tweetWid: String? = null,

    var fileName: String? = null,
    var extensionType: String? = null,

    var size: Long? = null,
    @JsonIgnore
    @Column(name = "file_byte", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    var fileByte: ByteArray? = null,

    var status: String? = "undo",
    @JsonIgnore
    var createTime: Long = Date().time
): Serializable
