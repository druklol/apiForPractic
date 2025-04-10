package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.Chat.Chat
import com.medofic.api.data.classes.Chat.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository:JpaRepository<Message, Long> {
    fun findByChatOrderByTimestampAsc(chat: Chat): List<Message>
}