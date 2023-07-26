package com.jesse.ohunelo.data.model

import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import java.util.Date

data class Notification(
    val id: Int = 0,
    val notificationContent: String,
    val addedOn: Date,
    val hasBeenRead: Boolean,
    val notificationTypeText: UiText,
    val notificationTypeIcon: UiDrawable
){
    fun formatAddedOn(): String{
        return DateUtils.getTimeStamp(addedOn)
    }
}
