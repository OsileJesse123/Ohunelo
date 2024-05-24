package com.jesse.ohunelo.workmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.NotificationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class DeleteNotificationsWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val notificationRepository: NotificationRepository,
    private val authenticationRepository: AuthenticationRepository
): CoroutineWorker(ctx, params){

    override suspend fun doWork(): Result {
         return if(authenticationRepository.isUserLoggedIn()){
            val notifications = notificationRepository.getNotifications().first()
            val notificationsSize = notifications.size
            val isDivisibleBy2 = notificationsSize % 2 == 0
            if (notificationsSize >= 20 && isDivisibleBy2){
                // Only half the notifications will be deleted from device. These deleted notifications
                // will older ones.
                val halfTheNotifications = notifications
                    .subList(notificationsSize/2, notificationsSize)
                notificationRepository.deleteNotifications(halfTheNotifications)
                Result.success()
            }
             Result.failure()
        } else {
            Result.failure()
        }
    }

    companion object{
        fun enableBiDailyDeletionOfNotifications(): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<DeleteNotificationsWorker>(2, TimeUnit.DAYS)
                .setInitialDelay(7, TimeUnit.DAYS)
                .build()
        }
    }
}