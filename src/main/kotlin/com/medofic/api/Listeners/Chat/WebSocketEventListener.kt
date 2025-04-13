package com.medofic.api.Listeners.Chat

import com.medofic.api.Configs.DebugMessagingTemplateDecorator
import com.medofic.api.data.classes.Chat.ChatNotification
import com.medofic.api.data.classes.Enums.NotificationType
import com.medofic.api.data.classes.Enums.UserRole
import com.medofic.api.data.classes.Repositories.UserRepository
import com.medofic.api.services.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener {
    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var messagingTemplate: DebugMessagingTemplateDecorator

    @Autowired
    private lateinit var chatService: ChatService

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val headerAccessor = StompHeaderAccessor.wrap(event.message)
        val userId = headerAccessor.sessionAttributes?.get("userId") as? Long ?: return

        val user = userRepository.findById(userId).orElse(null) ?: return

        user.isOnline = false
        userRepository.save(user)

        if (user.role == UserRole.ADMIN) {
            val chats = chatService.getAdminActiveChats(userId)

            for (chat in chats) {
                val notification = ChatNotification(
                    type = NotificationType.ADMIN_DISCONNECTED,
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
}