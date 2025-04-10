package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.Enums.UserRole
import com.medofic.api.data.classes.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository:JpaRepository<User, Long> {
    fun findBySnils(snils:String):User?
    fun findByRoleAndIsOnline(role: UserRole, isOnline: Boolean): List<User>
    fun findByRole(role:UserRole):List<User>
}