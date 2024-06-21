package com.example.cateringapp.remote.services

import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.req.OrderItemReq
import com.example.cateringapp.remote.req.OrderReq
import com.example.cateringapp.remote.req.ServedReq
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @GET("order/{orderId}")
    suspend fun updateOrderItemById(
        @Path("orderId") orderId: Int,
    ): Result<OrderRes>

    @GET("user/orders/history")
    suspend fun getUserOrdersHistory(): Result<List<OrderRes>>

    @GET("user/basket")
    suspend fun getUserBasket(): Result<OrderRes>

    @GET("business/{businessId}/orders/active")
    suspend fun getActiveBusinessOrders(
        @Path("businessId") businessId: Int,

    ): Result<List<OrderRes>>

    @GET("business/{businessId}/orders/history")
    suspend fun getBusinessOrdersHistory(
        @Path("businessId") businessId: Int,

    ): Result<List<OrderRes>>

    @POST("business/{businessId}/order/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("businessId") businessId: Int,
        @Path("orderId") orderId: Int,
        @Body statusReq: StatusReq,
    ): Result<Unit>

    @POST("user/basket/ask_for_payment")
    suspend fun basketAskForPayment(): Result<Unit>

    @POST("order/{orderId}/served")
    suspend fun updateOrderItemStatus(
        @Path("orderId") orderId: Int,
        @Body served: ServedReq,
    ): Result<Unit>

    @POST("order/{orderId}/orderItem")
    suspend fun addOrderItem(
        @Path("orderId") orderId: Int,
        @Body orderItems: List<OrderItemReq>,
    ): Result<Unit>

    @POST("business/{businessId}/order")
    suspend fun createOrderInitial(
        @Path("businessId") businessId: Int,
        @Body orderBody: OrderReq
    ): Result<OrderRes>

    @POST("business/{businessId}/manual_order")
    suspend fun createOrderManual(
        @Path("businessId") businessId: Int,
        @Body orderBody: OrderReq
    ): Result<OrderRes>
//
//    @POST("business/{businessId}/booking")
//    suspend fun createBooking(
//        @Path("businessId") businessId: Int,
//        @Body bookingReq: BookingReq,
//    ): Result<BookingRes>
//
//    @GET("business/{businessId}/order/{orderId}")
//    suspend fun getOrderById(
//        @Path("businessId") businessId: Int,
//        @Path("businessId") orderId: Int,
//    ): Result<OrderRes>
//
//    @POST("user/booking/{bookingId}/cancel")
//    suspend fun cancelByUserBooking(
//        @Path("bookingId") bookingId: Int,
//    ): Result<BookingRes>
//
//    @POST("business/{businessId}/booking")
//    suspend fun getBusinessBookings(
//        @Path("businessId") businessId: Int,
//    ): Result<List<BookingRes>>
}