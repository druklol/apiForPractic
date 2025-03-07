package com.medofic.api.data.classes.Enums

import kotlinx.serialization.Serializable

@Serializable
enum class AppointmentStatus {
    ANY,
    PLANNED,
    MISSED,
    COMPLETED
}