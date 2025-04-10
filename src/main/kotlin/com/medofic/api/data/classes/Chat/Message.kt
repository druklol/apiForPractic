package com.medofic.api.data.classes.Chat

import com.medofic.api.data.classes.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    val chat: Chat,

    @ManyToOne(fetch = FetchType.EAGER)
    val sender: User,

    val content: String,

    val timestamp: LocalDateTime = LocalDateTime.now(),
)

