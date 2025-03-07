package com.medofic.api.data.classes

import java.time.LocalDate

data class ProtocolInfo(
    val LPU: String,
    val investigationName: String,
    val doctorName: String,
    val date: LocalDate,
    val time: String,
)