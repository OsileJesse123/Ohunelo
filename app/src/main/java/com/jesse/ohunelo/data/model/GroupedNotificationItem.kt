package com.jesse.ohunelo.data.model

import com.jesse.ohunelo.util.UiText

data class GroupedNotificationItem(
    val header: UiText? = null,
    val notification: Notification? = null
) {
    fun isHeader(): Boolean{
        return header != null
    }
}
