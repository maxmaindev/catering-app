package com.example.cateringapp.remote.req

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoEditData(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String
)
