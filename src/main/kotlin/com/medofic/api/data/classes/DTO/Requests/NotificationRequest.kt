package com.medofic.api.data.classes.DTO.Requests

import com.medofic.api.data.classes.Notification

data class NotificationRequest(val snils: String, val notification: Notification)