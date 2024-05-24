package com.jesse.ohunelo.workmanager.initializer

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.jesse.ohunelo.BuildConfig
import com.jesse.ohunelo.util.DELETE_NOTIFICATIONS_WORKER_TAG
import com.jesse.ohunelo.util.NOTIFICATION_WORKER_TAG
import com.jesse.ohunelo.workmanager.worker.DeleteNotificationsWorker
import com.jesse.ohunelo.workmanager.worker.NotificationWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerInitializer: Initializer<WorkManager> {

    @Provides
    @Singleton
    override fun create(context: Context): WorkManager {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkManagerInitializerEntryPoint::class.java
        )
        val configuration = Configuration
            .Builder()
            .setWorkerFactory(entryPoint.hiltWorkerFactory())
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()
        if(!WorkManager.isInitialized())
            WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context).apply {
            enqueueUniquePeriodicWork(
                NOTIFICATION_WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                NotificationWorker.enableBiDailyNotifications()
            )
            enqueueUniquePeriodicWork(
                DELETE_NOTIFICATIONS_WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                DeleteNotificationsWorker.enableBiDailyDeletionOfNotifications()
            )
        }
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return mutableListOf()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface WorkManagerInitializerEntryPoint {
        fun hiltWorkerFactory(): HiltWorkerFactory
    }
}

object Sync {
    // This method is a workaround to manually initialize the sync process instead of relying on
    // automatic initialization with Androidx Startup. It is called from the app module's
    // Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        AppInitializer.getInstance(context)
            .initializeComponent(WorkManagerInitializer::class.java)
    }
}