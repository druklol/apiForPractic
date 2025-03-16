package com.medofic.api.data.classes

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

@Entity
@Table(name = "Notifications")
data class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    val id: Long? = null,
    @field:Pattern(regexp = "^\\d{3} \\d{3} \\d{3} \\d{2}$", message = "СНИЛС должен быть в формате ddd ddd ddd dd")
    val snils:String,
    val header: String,
    val description: String,
    val date: LocalDate = LocalDate.now()
)