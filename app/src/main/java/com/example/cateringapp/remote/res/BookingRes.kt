package com.example.cateringapp.remote.res

import com.example.cateringapp.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class BookingRes(
    val id: Int,
    val businessId: Int,
    val userId: Int,
    @Serializable(DateSerializer::class)// LONG
    val date: LocalDateTime,
    val tableSize: Int,
    val status: BookingStatus,
    val business: BusinessRes? = null,
    val user: UserInfoRes? = null
)

enum class BookingStatus(val status: String) {
    Confirmed("Confirmed"),
    Processing("Processing"),
    CanceledByUser("CanceledByUser"),
    CanceledOutOfSpace("CanceledOutOfSpace"),
    Finished("Finished")
}

fun translateBookingStatus(status: BookingStatus): String {
    return when (status) {
        BookingStatus.Confirmed -> "Підтверджено"
        BookingStatus.Processing -> "В обробці"
        BookingStatus.CanceledByUser -> "Скасовано користувачем"
        BookingStatus.CanceledOutOfSpace -> "Скасовано, немає місця"
        BookingStatus.Finished -> "Завершено"
    }
}