package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    suspend fun synchronizeNotifications(notificationType: NotificationType): OhuneloResult<Notification>

    fun getNotifications(): Flow<List<Notification>>

    suspend fun updateNotification(notification: Notification)

    suspend fun deleteNotifications(notifications: List<Notification>)

    fun getDenialCount(): Int

    fun updateDenialCount()

    fun resetDenialCount()

}