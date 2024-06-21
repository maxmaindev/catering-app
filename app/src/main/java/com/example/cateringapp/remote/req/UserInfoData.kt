package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoData(
    val name: String,
    val surname: String,
    val email: String,
    val password: String,
    val phone: String
)
