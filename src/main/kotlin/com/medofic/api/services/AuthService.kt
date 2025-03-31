package com.medofic.api.services

import com.medofic.api.data.classes.Repositories.UserRepository
import com.medofic.api.data.classes.User
import org.springframework.stereotype.Service

@Service
class AuthService(private val repository: UserRepository) {
    fun findUser(snils:String) = repository.findBySnils(snils)
    fun createUser(user: User) = repository.save(user)
}