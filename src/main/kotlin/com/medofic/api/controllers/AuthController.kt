package com.medofic.api.controllers

import com.medofic.api.data.classes.DTO.Requests.SnilsRequest
import com.medofic.api.data.classes.User
import com.medofic.api.services.AuthService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/auth")
class AuthController(private val authService: AuthService) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @Operation(summary = "Get info about user")
    @PostMapping("/login")
    private fun getUserInfo(snils: SnilsRequest): ResponseEntity<User> {
        logger.info("Попытка входа со снилсом ${snils.snils}")
        val user = authService.findUser(snils.snils)
        println(user)
        if (user == null) {
            logger.info("Пользователь со снилсом ${snils.snils} не найден")

            return ResponseEntity.badRequest().build()
        }
        logger.info("Вход со снилсом ${snils.snils} выполнен")
        return ResponseEntity.ok(user)
    }

    @Operation(summary = "Register user")
    @PostMapping("/register")
    private fun registerUser(user: User): ResponseEntity<String> {
        logger.info("Регистрация пользователя с снилсом: ${user.snils}")
        if (authService.findUser(user.snils) != null)
            return ResponseEntity.badRequest().body("User with ${user.snils} already exists")

        logger.info("Пользователь со снилсом ${user.snils} зарегистрирован")
        authService.createUser(user)
        return ResponseEntity.ok().build()
    }
}