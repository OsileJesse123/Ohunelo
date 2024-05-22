package com.jesse.ohunelo.data.model

import com.jesse.ohunelo.R
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.NotificationType
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import java.util.Date

data class Notification(
    val id: Int = 0,
    val notificationContent: String,
    val addedOn: Date,
    val hasBeenRead: Boolean,
    val notificationType: NotificationType
){
    fun formatAddedOn(): String{
        return DateUtils.getTimeStamp(addedOn)
    }

    fun notificationTypeUiText(): UiText = if (notificationType == NotificationType.FOOD_TRIVIA) UiText.StringResource(
        R.string.food_trivia) else UiText.StringResource(R.string.food_joke)

    fun notificationTypeIcon(): UiDrawable = if (notificationType == NotificationType.FOOD_TRIVIA) UiDrawable(
        R.drawable.food_trivia_icon) else UiDrawable(R.drawable.food_joke_icon)
}
