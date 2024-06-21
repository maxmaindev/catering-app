package com.example.cateringapp.remote.res

import kotlinx.serialization.Serializable

@Serializable
data class BusinessRes(
    val id: Int,
    val name: String,
    val address: String,
    val workingHours: String,
    val phone: String,
    val website: String,
    val listImgUrl: String
)
