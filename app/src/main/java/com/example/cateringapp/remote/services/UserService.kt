package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.req.UserInfoEditData
import com.example.cateringapp.remote.res.UserInfoRes
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface UserService {
    @GET("user/me")
    suspend fun getMyInfo(): Result<UserInfoRes>

    @PUT("user/me")
    suspend fun updateUserInfo(
        @Body userInfo: UserInfoEditData
    ): Result<UserInfoRes>
}