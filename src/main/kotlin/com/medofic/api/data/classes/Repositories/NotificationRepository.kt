package com.medofic.api.data.classes.Repositories

import com.medofic.api.data.classes.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long>{
    fun findBySnils(snils:String):List<Notification>
}