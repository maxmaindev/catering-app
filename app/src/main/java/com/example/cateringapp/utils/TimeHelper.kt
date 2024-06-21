package com.example.cateringapp.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun convertUtcToLocal(utcDateTime: LocalDateTime): String {
    // Convert LocalDateTime to ZonedDateTime in UTC
    val utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"))

    // Convert ZonedDateTime from UTC to the system's default time zone
    val localZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.systemDefault())

    // Define the desired date-time format
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")

    // Format the local ZonedDateTime to the desired string representation
    return localZonedDateTime.format(formatter)
}