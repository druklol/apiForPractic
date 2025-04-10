package com.medofic.api.data.classes.Chat

import com.medofic.api.data.classes.Enums.NotificationType

data class ChatNotification(
    val chatId: Long,
    val senderId: Long = 0,
    val message: String = "",
    val adminId: Long = 0,
    val adminUsername: String = "",
    val userId: Long = 0,
    val userUsername: String = "",
    val type: NotificationType
)
