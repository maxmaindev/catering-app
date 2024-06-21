package com.example.cateringapp.remote.res

import kotlinx.serialization.Serializable

@Serializable
data class OrderItemRes(
    val id: Int? = null,
    val orderId: Int,
    val menuItemId: Int,
    val count: Int,
    val menuItem: MenuItemRes,
    val isServed: Boolean,
)
