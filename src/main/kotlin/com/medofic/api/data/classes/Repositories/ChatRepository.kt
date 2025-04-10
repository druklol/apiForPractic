package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.Chat.Chat
import com.medofic.api.data.classes.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatRepository:JpaRepository<Chat,Long> {
    fun findByUserAndIsActive(user: User,isActive: Boolean): List<Chat>
    fun findByAdminAndIsActive(admin: User, isActive:Boolean): List<Chat>

    @Query("SELECT c FROM Chat c WHERE c.user.id = :userId AND c.admin.id = :adminId AND c.isActive = true")
    fun findActiveByUserIdAndAdminId(userId: Long, adminId: Long): Chat?
}