package com.jesse.ohunelo.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.local.database.NotificationDao
import com.jesse.ohunelo.data.local.models.NotificationEntity
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.network.data_source.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.NotificationType
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    private val notificationDao: NotificationDao
): NotificationRepository {

    override val groupedNotifications = notificationDao.getNotifications().map {
            notifications ->
        val notifs = notifications.map {
                notificationEntity ->
            notificationEntity.toNotification()
        }
        getGroupedNotificationItem(notifs)
    }.flowOn(defaultDispatcher)

    override fun getPagedNotifications(): Flow<PagingData<Notification>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, // Define the number of items per page
                enablePlaceholders = false
            ),
            pagingSourceFactory = { notificationDao.getPagedNotifications() }
        ).flow
            .map { pagingData ->
                pagingData.map { notificationEntity ->
                    notificationEntity.toNotification()
                }
            }
    }

    override suspend fun synchronizeNotifications(notificationType: NotificationType): OhuneloResult<Notification> {
         return withContext(ioDispatcher){
            try {
                // If notification type is FOOD_JOKE then get a random food joke else
                // get a random food trivia
                val notificationEntity = if (notificationType == NotificationType.FOOD_JOKE){
                    recipeNetworkDataSource.getRandomFoodJoke().toNotificationEntity()
                }
                else {
                    recipeNetworkDataSource.getRandomFoodTrivia().toNotificationEntity()
                }
                // Save notification locally
                notificationDao.insertNotification(notificationEntity)
                // Get notification from local storage and return it
                OhuneloResult.Success(notificationDao.getNotification(notificationEntity.notificationContent).toNotification())
            } catch (e: Exception){
                Timber.e("Failed to get food joke/trivia, Error: $e")
                OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.failed_to_get_food_joke))
            }
        }
    }
    override fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getNotifications().map {
                notifications ->
            notifications.map {
                    notificationEntity ->
                notificationEntity.toNotification()
            }
        }.flowOn(defaultDispatcher)
    }

    private fun getGroupedNotificationItem(notificationItems: List<Notification>): List<GroupedNotificationItem>{


        val groupedItems = mutableListOf<GroupedNotificationItem>()

        val todayItems = notificationItems.filter { notification -> DateUtils.isToday(notification.addedOn) }
        val yesterdayItems = notificationItems.filter { notification -> DateUtils.isYesterday(notification.addedOn) }
        val olderItems = notificationItems.filter {
                notification ->
            !DateUtils.isToday(notification.addedOn) && !DateUtils.isYesterday(notification.addedOn)
        }

        if (todayItems.isNotEmpty()) {
            groupedItems.add(GroupedNotificationItem(header = UiText.StringResource(R.string.today)))
            groupedItems.addAll(todayItems.map { GroupedNotificationItem(notification = it) })
        }

        if (yesterdayItems.isNotEmpty()) {
            groupedItems.add(GroupedNotificationItem(header = UiText.StringResource(R.string.yesterday)))
            groupedItems.addAll(yesterdayItems.map { GroupedNotificationItem(notification = it) })
        }

        if (olderItems.isNotEmpty()) {
            groupedItems.add(GroupedNotificationItem(header = UiText.StringResource(R.string.older)))
            groupedItems.addAll(olderItems.map { GroupedNotificationItem(notification = it) })
        }

        return groupedItems

    }

    override suspend fun updateNotification(notification: Notification) {
        withContext(ioDispatcher){
            notificationDao.updateNotification(NotificationEntity.fromNotification(notification))
        }
    }

}