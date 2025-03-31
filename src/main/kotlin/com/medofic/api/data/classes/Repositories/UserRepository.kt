package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository:JpaRepository<User, Long> {
    fun findBySnils(snils:String):User?
}