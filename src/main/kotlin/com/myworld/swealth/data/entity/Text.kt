package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "TEXT")
class Text(
    @javax.persistence.Id
    var id: String? = null,
    var bankName: String? = null,
    @JsonIgnore
    var bankLevel: String? = null,

    var name: String? = null,
    var date: String? = null,
    @Column(name = "url", columnDefinition = "MEDIUMTEXT")
    var url: String? = null,

    @JsonIgnore
    var typeMain: String? = null,
    @JsonIgnore
    var typeNext: String? = null,
    @JsonIgnore
    var typeOne: String? = null,
    @JsonIgnore
    var typeTwo: String? = null,
    @JsonIgnore
    var typeThree: String? = null,
    @JsonIgnore
    var status: String? = null,
    @JsonIgnore
    var createTime: String? = null,

    @Column(name = "photos", columnDefinition = "MEDIUMTEXT")
    var photos: String? = null,
    @Column(name = "content", columnDefinition = "LONGTEXT COMMENT '大文本格式'")
    var content: String? = null
): Serializable
