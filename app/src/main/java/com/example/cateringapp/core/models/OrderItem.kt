package com.example.cateringapp.core.models

import com.example.cateringapp.remote.res.MenuItemRes

data class OrderItem(
    val count: Int,
    val menuItem: MenuItemRes,
    val isServed: Boolean? = false,
)
