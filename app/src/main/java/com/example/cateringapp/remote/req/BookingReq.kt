package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class BookingReq(
    val id: Int? = null,
    val businessId: Int,
    val userId: Int,
    val date: Long,
    val tableSize: Int,
    val status: String? = null
)

