package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.BusinessEditReq
import com.example.cateringapp.remote.res.BusinessRes
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface BusinessService {
    @GET("business")
    suspend fun getAll(): Result<List<BusinessRes>>

    @GET("user/businesses")
    suspend fun getUserBusinesses(): Result<List<BusinessRes>>

    @GET("business/{id}")
    suspend fun getById(@Path("id") id: Int): Result<BusinessRes>

    @POST("business/{businessId}")
    suspend fun update(
        @Path("businessId") businessId: Int,
        @Body businessData: BusinessEditReq
    ): Result<BusinessRes>

    @POST("business")
    suspend fun create(
        @Body businessData: BusinessEditReq
    ): Result<BusinessRes>

    @Multipart
    @POST("business/{businessId}/image")
    suspend fun uploadBusinessImage(
        @Path("businessId") businessId: Int,
        @Part businessImage: MultipartBody.Part
    ): Result<Unit>
}