package com.medofic.api.Listeners.Chat

import com.medofic.api.Configs.DebugMessagingTemplateDecorator
import com.medofic.api.data.classes.Chat.ChatNotification
import com.medofic.api.data.classes.Enums.NotificationType
import com.medofic.api.data.classes.Enums.UserRole
import com.medofic.api.data.classes.Repositories.UserRepository
import com.medofic.api.services.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.time.LocalDateTime

@Component
class WebSocketEventListener {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var messagingTemplate: DebugMessagingTemplateDecorator

    @Autowired
    private lateinit var chatService: ChatService

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val userId = headerAccessor.sessionAttributes?.get("userId") as? Long ?: 0

        val user = userRepository.findById(userId).orElse(null) ?: return

        user.isOnline = true
        userRepository.save(user)

        if (user.role == UserRole.ADMIN) {
            val chats = chatService.getAdminActiveChats(userId)

            TODO()
            for (chat in chats) {
                val notification = ChatNotification(
                    type = NotificationType.ADMIN_CONNECTED,
                    chatId = chat.id,
                    adminId = user.id,
                    adminUsername = user.fullName
                )

                messagingTemplate.convertAndSendToUser(
                    chat.user.id.toString(),
                    "/queue/notifications",
                    notification
                )
            }
        }
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val userId = headerAccessor.sessionAttributes?.get("userId") as? Long ?: return

        // Получаем пользователя
        val user = userRepository.findById(userId).orElse(null) ?: return

        // Обновляем статус пользователя
        user.isOnline = false
        userRepository.save(user)

        // Если это администратор, отправляем уведомление всем пользователям
        if (user.role == UserRole.ADMIN) {
            // Находим все активные чаты этого администратора
            val chats = chatService.getAdminActiveChats(userId)

            // Отправляем уведомление в каждый чат
            for (chat in chats) {
                val notification = ChatNotification(
                    type = NotificationType.ADMIN_DISCONNECTED,
                    chatId = chat.id,
                    adminId = user.id,
                    adminUsername = user.fullName
                )

                // Отправляем уведомление пользователю
                messagingTemplate.convertAndSendToUser(
                    chat.user.id.toString(),
                    "/queue/notifications",
                    notification
                )
            }
        }
    }
}