package com.medofic.api.controllers

import com.medofic.api.data.classes.Chat.Chat
import com.medofic.api.data.classes.Chat.Message
import com.medofic.api.services.ChatService
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1/chat")
class ChatController(private val chatService: ChatService) {
    val logger = LoggerFactory.getLogger(ChatController::class.java)

    @PostMapping("/user/{userId}")
    fun createChat(@PathVariable userId: Long): ResponseEntity<Chat> {
        val chat = chatService.createChat(userId)
        return if (chat != null) {
            logger.info("Чат ${chat.id} создан успешно")
            ResponseEntity.ok(chat)
        } else {
            logger.warn("Нет доступных администраторов для создания чата")
            ResponseEntity.status(503).body(null) // 503 Service Unavailable если нет доступных администраторов
        }
    }

    @PutMapping("/{chatId}/close")
    fun closeChat(@PathVariable chatId: Long): ResponseEntity<Boolean> {
        val result = chatService.closeChat(chatId)
        logger.info("Чат $chatId закрыт")
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{chatId}")
    fun getChat(@PathVariable chatId: Long): ResponseEntity<Chat> {
        val chat = chatService.getChat(chatId) ?: run {
            logger.warn("Чат $chatId не найден")
            return ResponseEntity.notFound().build()
        }
        logger.info("Чат $chatId получен")
        return ResponseEntity.ok(chat)
    }

    @GetMapping("/user/{userId}/active")
    fun getUserActiveChats(@PathVariable userId: Long): ResponseEntity<List<Chat>> {
        val chats = chatService.getUserActiveChats(userId)
        logger.info("Активные чаты пользователя $userId получены")
        return ResponseEntity.ok(chats)
    }

    @GetMapping("/admin/{adminId}/active")
    fun getAdminActiveChats(@PathVariable adminId: Long): ResponseEntity<List<Chat>> {
        val chats = chatService.getAdminActiveChats(adminId)
        logger.info("Активные чаты администратора $adminId получены")
        return ResponseEntity.ok(chats)
    }

    @GetMapping("/{chatId}/messages")
    fun getChatMessages(@PathVariable chatId: Long): ResponseEntity<List<Message>> {
        val messages = chatService.getChatMessages(chatId)
        logger.info("Сообщения чата $chatId получены")
        return ResponseEntity.ok(messages)
    }
}