package com.medofic.api.data.classes

import com.medofic.api.data.classes.Enums.AppointmentStatus
import com.medofic.api.data.classes.Serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Appointment(
    val LPU: String,
    val investigationName: String,
    val doctorName: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val time: String,
    val status: AppointmentStatus
)