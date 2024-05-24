package com.jesse.ohunelo.data.model

import com.jesse.ohunelo.util.UiText

sealed class NotificationUIItem {
    class NotificationHeader(val header: UiText): NotificationUIItem()
    class NotificationItem(val item: Notification): NotificationUIItem()
}