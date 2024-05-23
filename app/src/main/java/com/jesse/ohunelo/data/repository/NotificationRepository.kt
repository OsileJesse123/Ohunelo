package com.jesse.ohunelo.data.repository

import androidx.paging.PagingData
import com.jesse.ohunelo.data.local.models.NotificationEntity
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.util.NotificationType
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    val groupedNotifications: Flow<List<GroupedNotificationItem>>

    fun getPagedNotifications(): Flow<PagingData<Notification>>

    suspend fun synchronizeNotifications(notificationType: NotificationType): OhuneloResult<Notification>

    fun getNotifications(): Flow<List<Notification>>

    suspend fun updateNotification(notification: Notification)

}