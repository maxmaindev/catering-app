package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemReq(
    val id: Int? = null,
    val orderId : Int,
    val menuItemId: Int,
    val count: Int
)