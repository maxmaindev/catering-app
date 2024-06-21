package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.LoginData
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.res.AuthData
import retrofit2.http.Body
import retrofit2.http.POST


interface AuthService {
    @POST("auth/login")
    suspend fun login(@Body loginData: LoginData): Result<AuthData>

    @POST("auth/register")
    suspend fun register(@Body registerData: UserInfoData): Result<AuthData>
}