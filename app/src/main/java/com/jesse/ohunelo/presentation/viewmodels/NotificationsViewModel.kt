package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.model.NotificationUIItem
import com.jesse.ohunelo.domain.repository.NotificationRepository
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val notificationsRepository: NotificationRepository
): ViewModel() {


    val notifications : StateFlow<List<NotificationUIItem>> = notificationsRepository.getNotifications()
        .map {
            getGroupedNotificationItem(it)
        }
        .flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), listOf())

    private fun getGroupedNotificationItem(notificationItems: List<Notification>): List<NotificationUIItem>{


       val groupedItems = mutableListOf<NotificationUIItem>()

       val todayItems = notificationItems.filter { notification -> DateUtils.isToday(notification.addedOn) }
       val yesterdayItems = notificationItems.filter { notification -> DateUtils.isYesterday(notification.addedOn) }
       val olderItems = notificationItems.filter {
               notification ->
           !DateUtils.isToday(notification.addedOn) && !DateUtils.isYesterday(notification.addedOn)
       }

       if (todayItems.isNotEmpty()) {
           groupedItems.add(NotificationUIItem.NotificationHeader(header = UiText.StringResource(R.string.today)))
           groupedItems.addAll(todayItems.map { NotificationUIItem.NotificationItem(item = it) })
       }

       if (yesterdayItems.isNotEmpty()) {
           groupedItems.add(NotificationUIItem.NotificationHeader(header = UiText.StringResource(R.string.yesterday)))
           groupedItems.addAll(yesterdayItems.map { NotificationUIItem.NotificationItem(item = it) })
       }

       if (olderItems.isNotEmpty()) {
           groupedItems.add(NotificationUIItem.NotificationHeader(header = UiText.StringResource(R.string.older)))
           groupedItems.addAll(olderItems.map { NotificationUIItem.NotificationItem(item = it) })
       }

       return groupedItems

    }

    fun shouldGuideUserToAppSettings(): Boolean = notificationsRepository.getDenialCount() > 0

    fun updateDenialCount(){
        notificationsRepository.updateDenialCount()
    }

    fun updateNotification(notification: Notification){
        viewModelScope.launch {
            notificationsRepository.updateNotification(notification)
        }
    }

    fun resetDenialCount(){
        // If the denial count is greater than 0, this means user at some point denied permission
        // permanently and had to grant permission from the app settings.
        if (notificationsRepository.getDenialCount() > 1)
            notificationsRepository.resetDenialCount()
    }
}
