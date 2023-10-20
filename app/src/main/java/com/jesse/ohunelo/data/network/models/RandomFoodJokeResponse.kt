package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.NotificationEntity
import com.jesse.ohunelo.util.NotificationType
import com.squareup.moshi.JsonClass
import java.util.Calendar

@JsonClass(generateAdapter = true)
data class RandomFoodJokeResponse(
    val text: String
){
    fun toNotificationEntity(): NotificationEntity{
        return NotificationEntity(notificationContent = text, notificationType = NotificationType.FOOD_JOKE,
            addedOn = Calendar.getInstance().time)
    }
}
