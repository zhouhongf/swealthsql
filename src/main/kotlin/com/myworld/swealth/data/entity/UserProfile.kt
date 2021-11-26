package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

// gender, 0是未定义，1是男，2是女

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "user_profile")
class UserProfile(
    @javax.persistence.Id
    var id: String? = null,

    var gender: Int? = 0,
    var intro: String? = null,

    var nickname: String? = null,
    var realname: String? = null,
    var email: String? = null,

    var idtype: String? = null,
    var position: String? = null,
    var industry: String? = null,
    var company: String? = null,
    var placebelong: String? = null,
    var address: String? = null,

    var offer: String? = null,
    var status: String? = "undo",

    @Column(name = "watch_user_wids", columnDefinition = "LONGTEXT")
    @JsonIgnore
    var watchUserWids: String? = "",

    @JsonIgnore
    var createTime: Long = Date().time,
    var updateTime: Long = Date().time
) : Serializable
