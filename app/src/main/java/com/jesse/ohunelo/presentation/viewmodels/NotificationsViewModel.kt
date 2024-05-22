package com.jesse.ohunelo.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.data.repository.NotificationRepository
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FETCH_SIZE = 9
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    private val notificationsRepository: NotificationRepository
): ViewModel() {

    private var cachedCount = 0

    private val _shouldFetch: MutableStateFlow<Boolean> = MutableStateFlow(true)

    private val _notifications : StateFlow<List<GroupedNotificationItem>> = notificationsRepository.getNotifications().map {
            notifications ->
        Log.e("Notifications", "Notifications Size: ${notifications.size}")
        getGroupedNotificationItem(notifications)
    }.flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), listOf())

    val notificationss: StateFlow<List<GroupedNotificationItem>> = combine(_notifications, _shouldFetch){
            notifications, shouldFetch ->
        /*if (shouldFetch){*/
            when{
                notifications.size <= FETCH_SIZE -> {
                    _shouldFetch.value = false
                    Log.e("Notifications", "Notifications just submitted 1st : ${notifications.size}")
                    notifications
                }
                notifications.size == cachedCount -> {
                    _shouldFetch.value = false
                    notifications
                }
                else -> {
                    cachedCount = when{
                        cachedCount == 0 -> FETCH_SIZE
                        else -> {
                            val sub = notifications.size - cachedCount
                            if (sub > FETCH_SIZE){
                                cachedCount + FETCH_SIZE
                            } else {
                                cachedCount + sub
                            }
                        }
                    }
                    Log.e("Notifications", "CachedCount: $cachedCount")
                    _shouldFetch.value = false
                    notifications.subList(0, cachedCount)
                }
            }
        /*} else{
            Log.e("Notifications", "Notifications just submitted: ${notifications.size}")
            notifications
        }*/
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), listOf())

    fun updateShouldFetch(){
        Log.e("Notifications", "Notifications Ran!!!!")
        if(_notifications.value.size > cachedCount){
            _shouldFetch.value = true
        }
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

    fun updateNotification(notification: Notification){
        viewModelScope.launch {
            notificationsRepository.updateNotification(notification)
        }
    }
}
