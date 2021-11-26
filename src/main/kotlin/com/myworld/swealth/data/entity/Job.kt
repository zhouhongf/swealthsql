package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "JOB")
class Job(
    @javax.persistence.Id
    var id: String? = null,
    var name: String? = null,
    var position: String? = null,
    var jobId: String? = null,
    var bankName: String? = null,
    var branchName: String? = null,
    var department: String? = null,
    var typeMain: String? = null,
    var typeNext: String? = null,
    var place: String? = null,
    var datePublish: String? = null,
    var dateClose: String? = null,
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    var content: String? = null,
    @Column(name = "requirement", columnDefinition = "MEDIUMTEXT")
    var requirement: String? = null,
    var education: String? = null,
    var major: String? = null,
    var recruitNum: String? = null,
    var yearsWork: String? = null,
    var salary: String? = null,
    @Column(name = "url", columnDefinition = "MEDIUMTEXT")
    var url: String? = null,
    @JsonIgnore
    var status: String? = null,
    @JsonIgnore
    var createTime: String? = null
): Serializable
