package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.UserFavor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface UserFavorRepository : JpaRepository<UserFavor, String> {
}
