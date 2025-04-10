package com.medofic.api.data.classes.Chat


import com.medofic.api.data.classes.User
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    val admin: User,

    @ManyToOne
    val user: User,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var closedAt: LocalDateTime? = null,
    var isActive: Boolean = true
    )