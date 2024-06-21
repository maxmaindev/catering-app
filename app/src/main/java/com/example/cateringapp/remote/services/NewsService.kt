package com.example.cateringapp.remote.services


import com.example.cateringapp.remote.req.NewsEditReq
import com.example.cateringapp.remote.res.NewsRes
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface NewsService {

    @GET("business/{businessId}/news")
    suspend fun getNews(@Path("businessId") businessId: Int): Result<List<NewsRes>>

    @POST("business/{businessId}/news")
    suspend fun create(
        @Path("businessId") businessId: Int,
        @Body itemData: NewsEditReq,
    ): Result<NewsRes>

    @POST("news/{newsId}")
    suspend fun update(
        @Path("newsId") newsId: Int,
        @Body itemData: NewsEditReq,
    ): Result<NewsRes>

    @DELETE("news/{newsId}")
    suspend fun delete(
        @Path("newsId") newsId: Int,
    ): Result<Unit>

    @Multipart
    @POST("news/{newsId}/image")
    suspend fun uploadNewsItemImage(
        @Path("newsId") newsId: Int,
        @Part newsImage: MultipartBody.Part,
    ): Result<Unit>
}