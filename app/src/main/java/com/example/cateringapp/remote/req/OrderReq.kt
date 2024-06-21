package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class OrderReq(
    val businessId: Int,
    val items: List<InitialOrderItemReq>,
    val info: String,

)