package com.jesse.ohunelo.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    fun isToday(date: Date): Boolean {
        val currentTime = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.time = date
        return currentTime.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                currentTime.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_YEAR, -1)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return yesterday.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
    }

    fun getTimeStamp(addedOn: Date): String{
        val currentTime = Calendar.getInstance().time
        val diffInMillis = currentTime.time - addedOn.time

        // Less than a minute ago
        if (diffInMillis < 60 * 1000) {
            return "${diffInMillis / 1000}s ago"
        }

        // Less than an hour ago
        if (diffInMillis < 60 * 60 * 1000) {
            return "${diffInMillis / (60 * 1000)}m ago"
        }

        // More than an hour ago
        if (diffInMillis < 24 * 60 * 60 * 1000) {
            return "${diffInMillis / (60 * 60 * 1000)}h ago"
        }

        // Date format for other days
        return SimpleDateFormat("d MMM", Locale.getDefault()).format(addedOn)
    }
}