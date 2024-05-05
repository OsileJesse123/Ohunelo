package com.jesse.ohunelo.presentation.uistates

import com.jesse.ohunelo.data.model.GroupedNotificationItem
import com.jesse.ohunelo.util.UiText

data class NotifcationsUiState(
    val notificationItems: List<GroupedNotificationItem> = listOf(),
    val errorMessage: UiText? = null,

)
