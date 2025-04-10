package com.medofic.api.data.classes

import com.fasterxml.jackson.annotation.JsonIgnore
import com.medofic.api.data.classes.Enums.Gender
import com.medofic.api.data.classes.Enums.UserRole
import jakarta.persistence.*
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

@Entity
@Table(name = "Users")
data class User (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    val id: Long = 0,
    @field:Pattern(regexp = "^\\d{3} \\d{3} \\d{3} \\d{2}$", message = "СНИЛС должен быть в формате ddd ddd ddd dd")
    @Column(unique = true)
    val snils:String,
    val age:Int,
    val fullName:String,
    val gender:Gender,
    val city:String,
    val address:String,
    val height:Int,
    val phoneNumber:String,
    val bloodGroup:String,
    val role: UserRole = UserRole.USER,
    val dateOfBirth: LocalDate,
    @JsonIgnore
    var isOnline: Boolean = false,
    @JsonIgnore
    var activeChatsCount: Int = 0
)