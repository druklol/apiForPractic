package com.medofic.api.data.classes.DTO.Requests

import com.medofic.api.data.classes.DispensaryObservation
import jakarta.validation.constraints.Pattern

data class DispensaryObservationRequest(
    @field:Pattern(regexp = "^\\d{3} \\d{3} \\d{3} \\d{2}$", message = "СНИЛС должен быть в формате ddd ddd ddd dd")
    val snils: String,
    val observation: DispensaryObservation
)