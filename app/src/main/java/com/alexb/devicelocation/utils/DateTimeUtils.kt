package com.alexb.devicelocation.utils

import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtils {

    fun localTimeFormatter(): SimpleDateFormat {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("Europe/Moscow")
        }
    }
}