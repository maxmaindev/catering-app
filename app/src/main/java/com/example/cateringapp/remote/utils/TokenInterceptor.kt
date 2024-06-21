package com.example.cateringapp.remote.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class CateringAccessTokenInterceptor(private val accessToken: Flow<String?>): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {  accessToken.firstOrNull()}
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(request)
    }

}