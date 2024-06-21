package com.example.cateringapp.remote.res

import com.example.cateringapp.core.UserRolePrefs
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
    val accessToken: String,
    val user: UserInfoRes
)
{
    fun getPrefsRole(): UserRolePrefs {
        if (user.role == UserRole.Admin.role){
            return UserRolePrefs.ADMIN
        }else{
            return UserRolePrefs.USER
        }
    }
}
