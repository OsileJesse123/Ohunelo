package com.jesse.ohunelo.workmanager.worker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
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
    @SuppressLint("InlinedApi")
    // POST_NOTIFICATIONS is automatically granted on API<33.
    override suspend fun doWork(): Result {
        val permissionDenied = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED
        // If the device uses API 33 or greater and notification permission is not granted, then
        // don't bother fetching notification.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && permissionDenied){
            return Result.failure()
        }
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