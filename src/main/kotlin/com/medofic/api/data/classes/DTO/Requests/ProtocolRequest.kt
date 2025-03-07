package com.medofic.api.data.classes.DTO.Requests

import jakarta.validation.constraints.Pattern

class ProtocolRequest
    (
    @field:Pattern(regexp = "^\\d{3} \\d{3} \\d{3} \\d{2}$", message = "СНИЛС должен быть в формате ddd ddd ddd dd")
    val snils: String,
    val fileName: String,
)