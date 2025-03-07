package com.medofic.api.data.classes.Enums

import kotlinx.serialization.Serializable

@Serializable
enum class AppointmentStatus(value: Int) {
    ANY(0),
    PLANNED(1),
    MISSED(2),
    COMPLETED(3)
}