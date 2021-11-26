package com.myworld.swealth.data.entity


import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "MANUAL")
class Manual(
    @javax.persistence.Id
    var id: String? = null,
    var ukey: String? = null,
    var bankName: String? = null,
    var fileType: String? = null,
    var fileSuffix: String? = null,
    var status: String? = null,
    var createTime: String? = null,
    @Column(name = "content", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    var content: ByteArray? = null
): Serializable
