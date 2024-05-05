package com.jesse.ohunelo.data.repository

import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.local.database.NotificationDao
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.network.data_source.RecipeNetworkDataSource
import com.jesse.ohunelo.data.network.models.OhuneloResult
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.UiText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val recipeNetworkDataSource: RecipeNetworkDataSource,
    private val notificationDao: NotificationDao
): NotificationRepository {

    override suspend fun getRandomFoodJoke(): OhuneloResult<Flow<List<Notification>>> {
        return withContext(ioDispatcher){
            try {
                // Get food joke from api
                val notificationResult = recipeNetworkDataSource.getRandomFoodJoke()
                // Save food joke locally
                notificationDao.insertNotification(notificationResult.toNotificationEntity())
                // Get notification from local storage, convert to regular notification and
                // return flow to UI
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Success(notifications)
            }
            catch (e: HttpException){
                Timber.e("HTTPError: $e, ErrorMessage: ${e.message()}")
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.failed_to_get_food_joke), data = notifications)
            }
            catch (e: Exception){
                Timber.e("GeneralError: $e, ErrorMessage: ${e.message}")
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.failed_to_get_food_joke), data = notifications)
            }
        }
    }

    override suspend fun getRandomFoodTrivia(): OhuneloResult<Flow<List<Notification>>> {
        return withContext(ioDispatcher){
            try {
                // Get food trivia from api
                val notificationResult = recipeNetworkDataSource.getRandomFoodTrivia()
                notificationDao.insertNotification(notificationResult.toNotificationEntity())
                // Get notification from local storage, convert to regular notification and
                // return flow to UI
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Success(notifications)
            }
            catch (e: HttpException){
                Timber.e("HTTPError: $e, ErrorMessage: ${e.message()}")
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.failed_to_get_food_joke), data = notifications)
            }
            catch (e: Exception){
                Timber.e("GeneralError: $e, ErrorMessage: ${e.message}")
                val notifications = withContext(defaultDispatcher){
                    notificationDao.getNotifications().map {
                            notifications ->
                        notifications.map {
                                notificationEntity ->
                            notificationEntity.toNotification()
                        }
                    }
                }
                OhuneloResult.Error(errorMessage = UiText.StringResource(R.string.failed_to_get_food_joke), data = notifications)
            }
        }
    }
}