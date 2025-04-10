package com.medofic.api.controllers

import com.medofic.api.Configs.DebugMessagingTemplateDecorator
import com.medofic.api.data.classes.Chat.ChatNotification
import com.medofic.api.data.classes.Chat.Requests.CheckSubscriptionRequest
import com.medofic.api.data.classes.Chat.Requests.CloseChatRequest
import com.medofic.api.data.classes.Chat.Requests.ConnectRequest
import com.medofic.api.data.classes.Chat.Requests.CreateChatRequest
import com.medofic.api.data.classes.DTO.Chat.MessageDTO
import com.medofic.api.data.classes.Enums.NotificationType
import com.medofic.api.services.ChatService
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.messaging.simp.user.SimpUserRegistry
import org.springframework.stereotype.Controller
import java.security.Principal
import java.time.LocalDateTime

@Controller
class WebSocketController(
    private val chatService: ChatService,
    private val messagingTemplate: DebugMessagingTemplateDecorator,
) {
    val logger = LoggerFactory.getLogger(WebSocketController::class.java)
    @MessageMapping("/chat.connect")
    fun connect(@Payload connectRequest: ConnectRequest, headerAccessor: SimpMessageHeaderAccessor) {
        val userId = connectRequest.userId
        headerAccessor.sessionAttributes?.put("userId", userId)
        logger.info("User $userId connected")
    }

    @MessageMapping("/chat.sendMessage")
    fun sendMessage(@Payload chatMessage: MessageDTO) {
        chatService.saveMessage(chatMessage) ?: return

        val chat = chatService.getChat(chatMessage.chatId) ?: return

        messagingTemplate.convertAndSendToUser(
            chat.user.id.toString(),
            "/queue/messages",
            chatMessage
        )

        messagingTemplate.convertAndSendToUser(
            chat.admin.id.toString(),
            "/queue/messages",
            chatMessage
        )

        val notification = ChatNotification(
            type = NotificationType.NEW_MESSAGE,
            chatId = chatMessage.chatId,
            senderId = chatMessage.senderId,
            message = chatMessage.content
        )

        val recipientId = if (chatMessage.senderId == chat.user.id)
            chat.admin.id
        else
            chat.user.id

        messagingTemplate.convertAndSendToUser(
            recipientId.toString(),
            "/queue/notifications",
            notification
        )

        logger.info("Message from ${chatMessage.senderId} sent to $recipientId content: ${chatMessage.content}")
    }

    @MessageMapping("/chat.create")
    fun createChat(@Payload request: CreateChatRequest) {
        chatService.createChat(request.userId)
        logger.info("Chat created for user ${request.userId}")
    }

    @MessageMapping("/chat.close")
    fun closeChat(@Payload request: CloseChatRequest) {
        chatService.closeChat(request.chatId)
        logger.info("Chat ${request.chatId} closed")
    }

    @MessageMapping("/chat.checkSubscription")
    fun checkSubscription(@Payload request: CheckSubscriptionRequest, headerAccessor: SimpMessageHeaderAccessor) {
        val notification = ChatNotification(
            type = NotificationType.SUBSCRIPTION_CHECK,
            chatId = 0,
            message = "Подписка работает! Время: ${LocalDateTime.now()}"
        )

        /*val allUsers = userRegistry.users
        println("Все подключённые пользователи: ${allUsers.map { it.name }}")

        println("=== ДИАГНОСТИКА ПРОБЛЕМЫ ===")
        println("Текущий пользователь: ${request.userId}")
        println("Текущая сессия: sessionId=${headerAccessor.sessionId}")
        println("Текущий principal: ${headerAccessor.user?.name}")*/

        /*val userId = request.userId.toString()
        val user = userRegistry.getUser(userId)
        if (user == null || user.sessions.isEmpty()) {
            println("User $userId has no active sessions, message not delivered")
        }
        else
            println("User HAVE sessions, message will be delivered + ${user.sessions}")*/
        messagingTemplate.convertAndSendToUser(
            request.userId.toString(),
            "/queue/notifications",
            notification
        )

        logger.info("Отправлено сообщение о проверке подписки пользователю ${request.userId}")
    }
}