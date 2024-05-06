package com.jesse.ohunelo.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jesse.ohunelo.data.local.models.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notification")
    fun getNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notification WHERE :notificationId = id")
    suspend fun getNotification(notificationId: Int): NotificationEntity

}