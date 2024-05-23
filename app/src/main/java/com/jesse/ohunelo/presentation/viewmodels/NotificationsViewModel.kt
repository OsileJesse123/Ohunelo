package com.jesse.ohunelo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.repository.NotificationRepository
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.NotificationType
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val notificationsRepository: NotificationRepository
): ViewModel() {

    val notifications : StateFlow<List<GroupedNotificationItem>> = notificationsRepository.groupedNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), listOf())

    val notificationss: StateFlow<PagingData<Notification>> =
        notificationsRepository.getPagedNotifications()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())
    /*val notifications : StateFlow<List<GroupedNotificationItem>> = notificationsRepository.getNotifications().map {
            notifications ->
        getGroupedNotificationItem(notifications.subList(0, 5))
    }.flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), listOf())*/

    val notifs = listOf(
        GroupedNotificationItem(
            header = UiText.StringResource(R.string.today),
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            header = UiText.StringResource(R.string.today),
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            header = UiText.StringResource(R.string.today),
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
        GroupedNotificationItem(
            notification = Notification(
                id = 1,
                notificationContent = "fjkdfjf",
                addedOn = Date(384848444),
                hasBeenRead = true,
                notificationType = NotificationType.FOOD_TRIVIA
            )
        ),
    )

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

    fun updateNotification(notification: Notification){
        viewModelScope.launch {
            notificationsRepository.updateNotification(notification)
        }
    }
}
