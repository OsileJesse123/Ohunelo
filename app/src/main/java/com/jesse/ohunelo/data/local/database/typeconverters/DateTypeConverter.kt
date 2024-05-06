package com.jesse.ohunelo.data.local.database.typeconverters

import androidx.room.TypeConverter
import java.util.*

class DateTypeConverter {

    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun toLong(value: Date): Long {
        return value.time
    }
}