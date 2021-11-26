package com.myworld.swealth.data.entity


import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "text_wordcloud")
class Wordcloud(
    @javax.persistence.Id
    var id: String? = null,
    @Column(name = "image", columnDefinition = "LONGBLOB COMMENT '文件格式'")
    var image: ByteArray? = null
): Serializable
