package com.myworld.swealth.data.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@DynamicInsert
@DynamicUpdate
@Entity
@Table(name = "user_favor")
class UserFavor(
    @javax.persistence.Id
    var id: String? = null,
    @Column(name = "favor_wealth", columnDefinition = "LONGTEXT")
    @JsonIgnore
    var favorWealths: String? = ""
) : Serializable
