package com.jesse.ohunelo.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Icon
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.jesse.ohunelo.R
import com.jesse.ohunelo.data.model.Notification
import javax.inject.Inject

class NotificationHelper @Inject constructor(
    private val context: Context
) {

    private val CHANNEL_FOOD_JOKE_AND_TRIVIA = "food_joke_and_trivia"

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(notification: Notification){

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.notificationFragment)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(context, CHANNEL_FOOD_JOKE_AND_TRIVIA)
            .setSmallIcon(R.drawable.ohunelo_logo)
            .setContentTitle(notification.notificationTypeText.asString(context))
            .setContentText(notification.notificationContent)
            .setLargeIcon(Icon.createWithResource(context, notification.notificationTypeIcon.resId))
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.notificationContent))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setColor(context.resources.getColor(R.color.orange_500, null))
        notificationManager.notify(notification.id, builder.build())
    }

    fun createNotificationChannel(){
        notificationManager.createNotificationChannel(NotificationChannel(
            CHANNEL_FOOD_JOKE_AND_TRIVIA,
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_description)
            }
        )
    }
}