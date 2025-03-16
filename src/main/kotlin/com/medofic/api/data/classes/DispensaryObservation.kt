package com.medofic.api.data.classes

import com.medofic.api.data.classes.Serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class DispensaryObservation(
    val LPU: String,
    @Serializable(with = LocalDateSerializer::class)
    val nextAppointmentDate: LocalDate,
    val doctorName: String,
    val disease: String
)