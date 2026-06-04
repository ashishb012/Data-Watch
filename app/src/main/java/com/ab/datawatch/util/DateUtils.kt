package com.ab.datawatch.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayString(): String {
        return LocalDate.now().format(dateFormatter)
    }

    fun getStartOfDayEpochMillis(): Long {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getEndOfDayEpochMillis(): Long {
        return LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
    }

    fun getStartOfMonthEpochMillis(): Long {
        return LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    fun getCurrentMonthString(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }
}
