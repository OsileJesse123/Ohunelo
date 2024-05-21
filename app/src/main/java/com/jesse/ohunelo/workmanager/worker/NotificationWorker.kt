package com.jesse.ohunelo.workmanager.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.NotificationRepository
import com.jesse.ohunelo.util.NotificationHelper
import com.jesse.ohunelo.util.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val notificationRepository: NotificationRepository,
    private val notificationHelper: NotificationHelper,
    private val authenticationRepository: AuthenticationRepository
): CoroutineWorker(ctx, params) {

    private val notificationTypes = listOf(NotificationType.FOOD_JOKE, NotificationType.FOOD_TRIVIA)
    override suspend fun doWork(): Result {
        return if(authenticationRepository.isUserLoggedIn()){
            when(val notificationsResult = notificationRepository.synchronizeNotifications(notificationTypes.random())){
                is OhuneloResult.Success -> {
                    notificationsResult.data?.let {
                            notification ->
                        notificationHelper.showNotification(notification)
                        Result.success()
                    } ?: Result.failure()
                }
                is OhuneloResult.Error -> {
                    Result.retry()
                }
            }
        } else {
            Result.failure()
        }
    }
}