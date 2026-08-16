package com.pedro.maschio.carcostsmanagement.utils

import com.pedro.maschio.carcostsmanagement.data.database.entities.RecurrenceType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object DateUtils {

    fun getDateStringFromMillis(millis: Long): String {
        return Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun calculateNextOccurrenceDelay(baseMillis: Long, recurrence: Int): Long {
        val zoneId = ZoneId.systemDefault()
        val localDate = Instant.ofEpochMilli(baseMillis).atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        val recurrenceType = RecurrenceType.fromInt(recurrence)
        var nextDate = when (recurrenceType) {
            RecurrenceType.MONTHLY -> localDate.plusMonths(1)
            RecurrenceType.YEARLY -> localDate.plusYears(1)
            RecurrenceType.NONE -> return -1L
        }

        // Ensure nextDate is in the future
        while (nextDate.isBefore(today) || nextDate.isEqual(today)) {
            nextDate = when (recurrenceType) {
                RecurrenceType.MONTHLY -> nextDate.plusMonths(1)
                RecurrenceType.YEARLY -> nextDate.plusYears(1)
                RecurrenceType.NONE -> break
            }
        }

        // Set to 9 AM
        val nextOccurrenceMillis = nextDate.atTime(9, 0).atZone(zoneId).toInstant().toEpochMilli()
        val delay = nextOccurrenceMillis - System.currentTimeMillis()

        return if (delay > 0) delay else 0L
    }
}
