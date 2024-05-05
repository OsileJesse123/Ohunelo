package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.jesse.ohunelo.data.local.models.NotificationEntity

class NotificationEntityTypeConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromNotificationEntity(notificationEntity: NotificationEntity): String{
        return gson.toJson(notificationEntity)
    }

    @TypeConverter
    fun toNotificationEntity(json: String): NotificationEntity {
        return gson.fromJson(json, NotificationEntity::class.java)
    }
}