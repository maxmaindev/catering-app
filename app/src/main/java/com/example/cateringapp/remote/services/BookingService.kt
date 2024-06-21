package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.req.LoginData
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.BookingRes
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingService {
    @POST("business/{businessId}/booking")
    suspend fun createBooking(
        @Path("businessId") businessId: Int,
        @Body bookingReq: BookingReq,
    ): Result<BookingRes>

    @GET("user/bookings/all")
    suspend fun getUserBookings(): Result<List<BookingRes>>

    @GET("user/bookings/active")
    suspend fun getActiveUserBookings(): Result<List<BookingRes>>

    @GET("user/bookings/finished")
    suspend fun getHistoryUserBookings(): Result<List<BookingRes>>

    @GET("business/{businessId}/booking/active")
    suspend fun getActiveBusinessBookings(
        @Path("businessId") businessId: Int,
    ): Result<List<BookingRes>>

    @GET("business/{businessId}/booking/finished")
    suspend fun getHistoryBusinessBookings(
        @Path("businessId") businessId: Int,
    ): Result<List<BookingRes>>


    @POST("user/booking/{bookingId}/cancel")
    suspend fun cancelByUserBooking(
        @Path("bookingId") bookingId: Int,
    ): Result<BookingRes>

    @POST("booking/{bookingId}/status")
    suspend fun updateStatusBooking(
        @Path("bookingId") bookingId: Int,
        @Body status: StatusReq,
    ): Result<Unit>

    @GET("business/{businessId}/booking")
    suspend fun getBusinessBookings(
        @Path("businessId") businessId: Int,
    ): Result<List<BookingRes>>
}