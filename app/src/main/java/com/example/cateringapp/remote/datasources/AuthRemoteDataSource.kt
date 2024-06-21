package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.LoginData
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.services.AuthService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler


class AuthRemoteDataSource(
    private val authService: AuthService
) {

    suspend fun login(loginData: LoginData): NetworkResult<AuthData> {
        val responseResult = authService.login(loginData)
        return resultHandler{ responseResult }
    }

    suspend fun register(userInfoData: UserInfoData): NetworkResult<AuthData>{
        val responseResult = authService.register(userInfoData)
        return resultHandler{ responseResult }
    }


}
