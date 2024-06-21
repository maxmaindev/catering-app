package com.example.cateringapp.remote.res

import com.example.cateringapp.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class OrderRes(
    val id: Int,
    val businessId: Int,
    val userId: Int,
    @Serializable(DateSerializer::class)
    val date: LocalDateTime,
    val status: OrderStatus,
    val info: String,
    val business: BusinessRes? = null,
    val user: UserInfoRes? = null,
    val items: List<OrderItemRes>? = null
)
enum class OrderStatus(val status: String) {
    Processing("Processing"),//single element in user orders
    AwaitingPayments("AwaitingPayments"),
    Paid("Paid"),
}

fun translateOrderStatus(status: OrderStatus): String {
    return when (status) {
        OrderStatus.Processing -> "В обробці"
        OrderStatus.AwaitingPayments -> "Очікує оплати"
        OrderStatus.Paid -> "Оплачено"
    }
}