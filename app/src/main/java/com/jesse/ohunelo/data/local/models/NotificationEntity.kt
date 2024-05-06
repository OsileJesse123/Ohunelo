package com.jesse.ohunelo.data.local.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.util.NotificationType
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import java.util.Date

@Entity(tableName = "notification")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "notification_type")
    val notificationType: NotificationType,
    @ColumnInfo(name = "notification_content")
    val notificationContent: String,
    @ColumnInfo(name = "added_on")
    val addedOn: Date,
    @ColumnInfo(name = "has_been_read")
    val hasBeenRead: Boolean = false
){

    companion object {
        fun fromNotification(notification: Notification): NotificationEntity = NotificationEntity(
            id = notification.id,
            notificationType = if (notification.notificationTypeIcon == UiDrawable(R.drawable.food_trivia_icon)) NotificationType.FOOD_TRIVIA else NotificationType.FOOD_JOKE,
            addedOn = notification.addedOn,
            notificationContent = notification.notificationContent,
            hasBeenRead = notification.hasBeenRead
        )
    }
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
