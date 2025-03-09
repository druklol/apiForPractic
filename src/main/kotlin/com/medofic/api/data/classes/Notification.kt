package com.medofic.api.data.classes

import kotlinx.serialization.Serializable

@Serializable
data class Notification(val header: String, val description: String)