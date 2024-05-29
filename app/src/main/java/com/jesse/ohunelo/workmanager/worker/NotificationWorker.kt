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
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.data.repository.AuthenticationRepository
import com.jesse.ohunelo.data.repository.NotificationRepository
import com.jesse.ohunelo.util.NotificationHelper
import com.jesse.ohunelo.util.NotificationType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

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

    companion object{
        fun enableBiDailyNotifications(): PeriodicWorkRequest{
            val constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val notificationWorkerRequest = PeriodicWorkRequestBuilder<NotificationWorker>(2, TimeUnit.DAYS)
                .setConstraints(constraints)
                .setInitialDelay(2, TimeUnit.DAYS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                .build()
            return notificationWorkerRequest
        }
    }
}