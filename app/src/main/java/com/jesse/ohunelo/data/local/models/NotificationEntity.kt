package com.jesse.ohunelo.data.local.models

import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.NotificationType
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import java.util.Date

data class NotificationEntity(
    val id: Int = 0,
    val notificationType: NotificationType,
    val notificationContent: String,
    val addedOn: Date,
    val hasBeenRead: Boolean = false
){
    fun toNotification(): Notification {
        return Notification(id = id, notificationContent = notificationContent, addedOn = addedOn,
            hasBeenRead = hasBeenRead, notificationTypeText = getNotificationTypeText(),
            notificationTypeIcon = getNotificationTypeIcon())
    }



    private fun getNotificationTypeText(): UiText{
        return when(notificationType){
            NotificationType.FOOD_TRIVIA -> {UiText.StringResource(R.string.food_trivia)}
            NotificationType.FOOD_JOKE -> {UiText.StringResource(R.string.food_joke)}
        }
    }

    private fun getNotificationTypeIcon(): UiDrawable{
        return when(notificationType){
            NotificationType.FOOD_TRIVIA -> {UiDrawable(R.drawable.food_trivia_icon)}
            NotificationType.FOOD_JOKE -> {UiDrawable(R.drawable.food_joke_icon)}
        }
    }
}
