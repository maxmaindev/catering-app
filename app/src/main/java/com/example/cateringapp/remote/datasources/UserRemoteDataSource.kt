package com.example.cateringapp.remote.datasources

import android.util.Log
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.req.UserInfoEditData
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.res.UserInfoRes
import com.example.cateringapp.remote.services.BusinessService
import com.example.cateringapp.remote.services.UserService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler
import retrofit2.http.Body


class UserRemoteDataSource(
    private val userService: UserService
) {

    suspend fun getMyInfo(): NetworkResult<UserInfoRes> {
        val responseResult = userService.getMyInfo()
        return resultHandler{ responseResult }
    }

    suspend fun updateUserInfo(
        userData: UserInfoEditData
    ): NetworkResult<UserInfoRes> {
        val responseResult = userService.updateUserInfo(userData)
        return resultHandler{ responseResult }
    }




}
