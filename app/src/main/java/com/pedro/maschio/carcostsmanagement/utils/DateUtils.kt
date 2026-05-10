package com.pedro.maschio.carcostsmanagement.utils

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {
//    fun getTodayDate(): String {
//        val currentTimestamp = System.currentTimeMillis()
//        val date = java.util.Date(currentTimestamp)
//
//        val firstApiFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
//            .format(firstApiFormat)
//    }

    fun getDateStringFromMillis(millis: Long): String {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate() // ZoneId.systemDefault()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }
}