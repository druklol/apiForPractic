package com.medofic.api.Configs

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class DebugMessagingTemplateDecorator(@Autowired private val messagingTemplate: SimpMessagingTemplate) {
    val logger = LoggerFactory.getLogger(DebugMessagingTemplateDecorator::class.java)

    fun convertAndSendToUser(user: String, destination: String, payload: Any) {
        logger.info("Отправка сообщения пользователю $user на $destination")
        try {
            messagingTemplate.convertAndSendToUser(user, destination, payload)
            logger.info("Сообщение успешно отправлено пользователю $user")
        } catch (e: Exception) {
            logger.warn("Ошибка при отправке сообщения пользователю $user: ${e.message}")
            throw e
        }
    }

    fun convertAndSendToUser(
        user: String,
        destination: String,
        payload: Any,
        headers: MessageHeaders
    ) {
        logger.info("Отправка сообщения пользователю $user на $destination с заголовками")
        try {
            messagingTemplate.convertAndSendToUser(user, destination, payload, headers)
            logger.info("Сообщение успешно отправлено пользователю $user с заголовками")
        } catch (e: Exception) {
            logger.warn("Ошибка при отправке сообщения пользователю $user с заголовками: ${e.message}")
            throw e
        }
    }

    fun convertAndSendToUser(
        user: String,
        destination: String,
        payload: Any,
        headers: Map<String, Any>
    ) {
        logger.info("Отправка сообщения пользователю $user на $destination с map заголовками")
        try {
            messagingTemplate.convertAndSendToUser(user, destination, payload, headers)
            logger.info("Сообщение успешно отправлено пользователю $user с map заголовками")
        } catch (e: Exception) {
            logger.warn("Ошибка при отправке сообщения пользователю $user с map заголовками: ${e.message}")
            throw e
        }
    }
}