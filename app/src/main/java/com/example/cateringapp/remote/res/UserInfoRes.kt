package com.example.cateringapp.remote.res

import com.example.cateringapp.core.AppModule
import com.example.cateringapp.utils.AppConsts
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoRes(
    val id: Int,
    val name: String,
    val surname: String,
    val email: String,
    val phone: String,
    val role: String
)

enum class UserRole(val role: String) {
    User("user"),
    Admin("admin"),
}


