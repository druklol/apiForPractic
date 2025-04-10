package com.medofic.api.services

import com.medofic.api.Configs.DebugMessagingTemplateDecorator
import com.medofic.api.data.classes.Chat.Chat
import com.medofic.api.data.classes.Chat.ChatNotification
import com.medofic.api.data.classes.Chat.Message
import com.medofic.api.data.classes.DTO.Chat.MessageDTO
import com.medofic.api.data.classes.Enums.NotificationType
import com.medofic.api.data.classes.Repositories.ChatRepository
import com.medofic.api.data.classes.Repositories.MessageRepository
import com.medofic.api.data.classes.Repositories.UserRepository
import jakarta.transaction.Transactional
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val userService: ChatUserService,
    private val messagingTemplate: DebugMessagingTemplateDecorator
) {

    @Transactional
    fun createChat(userId: Long): Chat? {
        val user = userRepository.findById(userId).orElse(null) ?: return null

        // Находим доступного онлайн-администратора с наименьшим количеством активных чатов
        val onlineAdmins = userService.findOnlineAdmins()
        if (onlineAdmins.isEmpty()) {
            return null
        }

        val admin = onlineAdmins.minByOrNull { it.activeChatsCount } ?: return null

        // Проверяем, существует ли уже активный чат между этим пользователем и администратором
        val existingChat = chatRepository.findActiveByUserIdAndAdminId(userId, admin.id)
        if (existingChat != null) {
            return existingChat
        }

        // Создаем новый чат
        val chat = Chat(
            admin = admin,
            user = user
        )

        val savedChat = chatRepository.save(chat)

        // Увеличиваем счетчик активных чатов для администратора
        userService.incrementActiveChatsCount(admin.id)

        // Отправляем уведомление о создании чата
        val notification = ChatNotification(
            type = NotificationType.CHAT_CREATED,
            chatId = savedChat.id,
            userId = user.id,
            userUsername = user.fullName,
            adminId = admin.id,
            adminUsername = admin.fullName
        )

        // Отправляем уведомление администратору
        messagingTemplate.convertAndSendToUser(
            admin.id.toString(),
            "/queue/notifications",
            notification
        )

        // Отправляем уведомление пользователю
        messagingTemplate.convertAndSendToUser(
            user.id.toString(),
            "/queue/notifications",
            notification
        )

        return savedChat
    }

    @Transactional
    fun closeChat(chatId: Long): Boolean {
        val chat = chatRepository.findById(chatId).orElse(null) ?: return false

        if (!chat.isActive) {
            return false
        }

        // Закрываем чат
        chat.isActive = false
        chat.closedAt = LocalDateTime.now()
        chatRepository.save(chat)

        // Уменьшаем счетчик активных чатов для администратора
        userService.decrementActiveChatsCount(chat.admin.id)

        // Отправляем уведомление о закрытии чата
        val notification = ChatNotification(
            type = NotificationType.CHAT_CLOSED,
            chatId = chatId
        )

        // Отправляем уведомление администратору
        messagingTemplate.convertAndSendToUser(
            chat.admin.id.toString(),
            "/queue/notifications",
            notification
        )

        // Отправляем уведомление пользователю
        messagingTemplate.convertAndSendToUser(
            chat.user.id.toString(),
            "/queue/notifications",
            notification
        )

        return true
    }

    fun getChat(chatId: Long): Chat? {
        return chatRepository.findById(chatId).orElse(null)
    }

    fun getUserActiveChats(userId: Long): List<Chat> {
        val user = userRepository.findById(userId).orElse(null) ?: return emptyList()
        return chatRepository.findByUserAndIsActive(user, true)
    }

    fun getAdminActiveChats(adminId: Long): List<Chat> {
        val admin = userRepository.findById(adminId).orElse(null) ?: return emptyList()
        return chatRepository.findByAdminAndIsActive(admin, true)
    }

    fun getChatMessages(chatId: Long): List<Message> {
        val chat = chatRepository.findById(chatId).orElse(null) ?: return emptyList()
        return messageRepository.findByChatOrderByTimestampAsc(chat)
    }

    @Transactional
    fun saveMessage(chatMessage: MessageDTO): Message? {
        val chat = chatRepository.findById(chatMessage.chatId).orElse(null) ?: return null
        val sender = userRepository.findById(chatMessage.senderId).orElse(null) ?: return null

        // Проверяем, активен ли чат
        if (!chat.isActive) {
            return null
        }

        val message = Message(
            chat = chat,
            sender = sender,
            content = chatMessage.content,
            timestamp = LocalDateTime.now()
        )

        return messageRepository.save(message)
    }
}