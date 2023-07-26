package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.data.model.Notification
import com.jesse.ohunelo.di.DefaultDispatcher
import com.jesse.ohunelo.di.IODispatcher
import com.jesse.ohunelo.util.DateUtils
import com.jesse.ohunelo.util.UiDrawable
import com.jesse.ohunelo.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val _notificationItems: MutableStateFlow<List<GroupedNotificationItem>> =
        MutableStateFlow(listOf())
    val notificationItems get() = _notificationItems.asStateFlow()

    init {
        updateNotificationsItems()
    }

    private fun updateNotificationsItems(){
        viewModelScope.launch(ioDispatcher) {

            val notificationItems = (1..20).map {
                Notification(id = it,
                    notificationContent = "The red food-coloring carmine used in Skittles and other" +
                            " candies is made from boiled cochineal bugs, a type of beetle.",
                    addedOn = Calendar.getInstance().time,
                    hasBeenRead = false,
                    notificationTypeIcon = UiDrawable(R.drawable.food_joke_icon),
                    notificationTypeText = UiText.StringResource(R.string.food_joke)
                )
            }
            val groupedItems = mutableListOf<GroupedNotificationItem>()

           withContext(defaultDispatcher){
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

               _notificationItems.update { groupedItems }
           }


        }
    }

}