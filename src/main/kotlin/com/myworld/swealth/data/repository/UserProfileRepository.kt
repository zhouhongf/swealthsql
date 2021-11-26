package com.myworld.swealth.data.repository

import com.myworld.swealth.data.entity.UserProfile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Transactional
@Repository
interface UserProfileRepository : JpaRepository<UserProfile, String>{
    fun existsByIdAndWatchUserWidsContaining(id: String, watchUserWid: String): Boolean
}
