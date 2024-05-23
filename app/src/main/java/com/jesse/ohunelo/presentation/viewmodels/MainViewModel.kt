package com.jesse.ohunelo.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.jesse.ohunelo.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    notificationRepository: NotificationRepository
): ViewModel() {

    val unreadNotificationsCount: Flow<Int> = notificationRepository.groupedNotifications.flatMapLatest {
        notifications ->
        flow {
            emit(notifications.size)
            //emit(notifications.filter { !it.hasBeenRead }.size)
        }
    }
}