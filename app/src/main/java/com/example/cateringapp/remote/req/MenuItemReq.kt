package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemReq(
    val name: String,
    val description: String,
    val metrics: String,
    val price: Float
)