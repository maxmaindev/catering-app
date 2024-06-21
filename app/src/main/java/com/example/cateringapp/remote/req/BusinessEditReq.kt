package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class BusinessEditReq(
    val name: String,
    val address: String,
    val workingHours: String,
    val phone: String,
    val website: String? = null
)
