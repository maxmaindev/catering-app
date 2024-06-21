package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class NewsEditReq(
    val title: String,
    val text: String,
)