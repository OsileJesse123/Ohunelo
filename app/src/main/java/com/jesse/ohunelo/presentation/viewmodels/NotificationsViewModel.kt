package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.repository.NotificationRepository
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val notificationsRepository: NotificationRepository
): ViewModel() {

    val notifications : LiveData<List<GroupedNotificationItem>> = notificationsRepository.getNotifications().map {
            notifications ->
        getGroupedNotificationItem(notifications)
    }.asLiveData()

    private suspend fun getGroupedNotificationItem(notificationItems: List<Notification>): List<GroupedNotificationItem>{
       return withContext(defaultDispatcher) {
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

           groupedItems
       }
    }

    fun updateNotification(notification: Notification){
        viewModelScope.launch {
            notificationsRepository.updateNotification(notification)
        }
    }
}