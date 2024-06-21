package com.example.cateringapp.remote.res

import kotlinx.serialization.Serializable

@Serializable
data class MenuItemRes(
    val id: Int,
    val name: String,
    val description: String,
    val metrics: String,
    val price: Float,
    val imgUrl: String,
    val businessId: Int
)