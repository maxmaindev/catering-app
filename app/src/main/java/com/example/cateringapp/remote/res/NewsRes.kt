package com.example.cateringapp.remote.res

import com.example.cateringapp.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class NewsRes(
    val id: Int,
    val businessId: Int,
    @Serializable(DateSerializer::class)
    val date: LocalDateTime,
    val title: String,
    val text: String,
    val imgUrl: String,
    val business: BusinessRes? = null
)
