package com.example.cateringapp.remote.req

import com.example.cateringapp.remote.res.MenuItemRes
import kotlinx.serialization.Serializable

@Serializable
data class InitialOrderItemReq(
    val menuItemId: Int,
    val count: Int
)