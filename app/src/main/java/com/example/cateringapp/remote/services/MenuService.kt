package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.res.MenuItemRes
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MenuService {

    @GET("business/{businessId}/menu")
    suspend fun getMenu(@Path("businessId") id: Int): Result<List<MenuItemRes>>

    @POST("business/{businessId}/menu")
    suspend fun create(
        @Path("businessId") businessId: Int,
        @Body itemData: MenuItemReq
    ): Result<MenuItemRes>
    @POST("business/{businessId}/menu/{menuItemId}")
    suspend fun update(
        @Path("businessId") businessId: Int,
        @Path("menuItemId") menuItemId: Int,
        @Body itemData: MenuItemReq
    ): Result<MenuItemRes>

    @DELETE("business/{businessId}/menu/{menuItemId}")
    suspend fun delete(
        @Path("businessId") businessId: Int,
        @Path("menuItemId") menuItemId: Int
    ): Result<Unit>

    @Multipart
    @POST("business/{businessId}/menu/{menuItemId}/image")
    suspend fun uploadMenuItemImage(
        @Path("businessId") businessId: Int,
        @Path("menuItemId") menuItemId: Int,
        @Part profileImage: MultipartBody.Part
    ): Result<Unit>
}