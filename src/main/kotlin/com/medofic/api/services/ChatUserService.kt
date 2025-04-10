package com.medofic.api.services

import com.medofic.api.data.classes.Enums.UserRole
import com.medofic.api.data.classes.Repositories.UserRepository
import com.medofic.api.data.classes.User
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ChatUserService(private val userRepository: UserRepository) {
    fun findById(id: Long): User? {
        return userRepository.findById(id).orElse(null)
    }

    fun findBySnils(snils: String): User? {
        return userRepository.findBySnils(snils)
    }

    fun findAllAdmins(): List<User> {
        return userRepository.findByRole(UserRole.ADMIN)
    }

    fun findOnlineAdmins(): List<User> {
        return userRepository.findByRoleAndIsOnline(UserRole.ADMIN, true)
    }

    @Transactional
    fun updateUserOnlineStatus(userId: Long, isOnline: Boolean): User? {
        val user = userRepository.findById(userId).orElse(null) ?: return null
        user.isOnline = isOnline
        return userRepository.save(user)
    }

    @Transactional
    fun incrementActiveChatsCount(userId: Long): User? {
        val user = userRepository.findById(userId).orElse(null) ?: return null
        user.activeChatsCount++
        return userRepository.save(user)
    }

    @Transactional
    fun decrementActiveChatsCount(userId: Long): User? {
        val user = userRepository.findById(userId).orElse(null) ?: return null
        if (user.activeChatsCount > 0) {
            user.activeChatsCount--
        }
        return userRepository.save(user)
    }
}