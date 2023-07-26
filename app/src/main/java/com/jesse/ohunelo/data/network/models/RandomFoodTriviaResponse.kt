package com.jesse.ohunelo.data.network.models

import com.jesse.ohunelo.data.local.models.NotificationEntity
import com.jesse.ohunelo.util.NotificationType
import java.util.Calendar

data class RandomFoodTriviaResponse(
    val text: String
){
    fun toNotificationEntity(): NotificationEntity {
        return NotificationEntity(notificationContent = text, notificationType = NotificationType.FOOD_TRIVIA,
            addedOn = Calendar.getInstance().time)
    }
}
