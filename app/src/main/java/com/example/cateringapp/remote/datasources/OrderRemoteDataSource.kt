package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.req.OrderItemReq
import com.example.cateringapp.remote.req.OrderReq
import com.example.cateringapp.remote.req.ServedReq
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.services.OrderService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler

class OrderRemoteDataSource(
    private val orderService: OrderService,
) {

    suspend fun getOrderItemByID(
        orderId: Int
    ): NetworkResult<OrderRes> {
        val responseResult = orderService.updateOrderItemById(orderId)
        return resultHandler { responseResult }
    }

    suspend fun getUserBasket(): NetworkResult<OrderRes> {
        val responseResult = orderService.getUserBasket()
        return resultHandler { responseResult }
    }

    suspend fun getUserOrdersHistory(): NetworkResult<List<OrderRes>> {
        val responseResult = orderService.getUserOrdersHistory()
        return resultHandler { responseResult }
    }

    suspend fun getBusinessOrderHistory(
        businessId: Int,
    ): NetworkResult<List<OrderRes>> {
        val responseResult = orderService.getBusinessOrdersHistory(businessId)
        return resultHandler { responseResult }
    }

    suspend fun getBusinessOrderActive(
        businessId: Int,
    ): NetworkResult<List<OrderRes>> {
        val responseResult = orderService.getActiveBusinessOrders(businessId)
        return resultHandler { responseResult }
    }

    suspend fun updateOrderItemServedStatus(
        orderId: Int,
        isServed: Boolean,
    ): NetworkResult<Unit> {
        val responseResult = orderService.updateOrderItemStatus(orderId, ServedReq(isServed))
        return resultHandler { responseResult }
    }

    suspend fun updateOrderStatus(
        businessId: Int,
        orderId: Int,
        statusBody: StatusReq,
    ): NetworkResult<Unit> {
        val responseResult = orderService.updateOrderStatus(
            businessId = businessId,
            orderId = orderId,
            statusReq = statusBody
        )
        return resultHandler { responseResult }
    }

    suspend fun basketAskForPayment(): NetworkResult<Unit>{
        val responseResult = orderService.basketAskForPayment()
        return resultHandler { responseResult }
    }


    suspend fun addOrderItem(
        orderId: Int,
        orderItems: List<OrderItemReq>,
    ): NetworkResult<Unit> {
        val responseResult = orderService.addOrderItem(
            orderId = orderId,
            orderItems = orderItems
        )
        return resultHandler { responseResult }
    }
    suspend fun createOrderInitial(
        businessId: Int,
        orderBody: OrderReq,
    ): NetworkResult<OrderRes> {
        val responseResult = orderService.createOrderInitial(
            businessId = businessId,
            orderBody = orderBody
        )
        return resultHandler { responseResult }
    }
    suspend fun createOrderManual(
        businessId: Int,
        orderBody: OrderReq,
    ): NetworkResult<OrderRes> {
        val responseResult = orderService.createOrderManual(
            businessId = businessId,
            orderBody = orderBody
        )
        return resultHandler { responseResult }
    }
}
//    suspend fun getUserBookings(): NetworkResult<List<BookingRes>> {
//        val responseResult = bookingService.getUserBookings()
//        return resultHandler{ responseResult }
//    }
//
//    suspend fun getBusinessBooking(
//        businessId: Int,
//    ): NetworkResult<List<BookingRes>> {
//        val responseResult = bookingService.getUserBookings()
//        return resultHandler{ responseResult }
//    }
//
//    suspend fun createUserBookings(
//        businessId: Int,
//        booking: BookingReq
//    ): NetworkResult<BookingRes> {
//        val responseResult = bookingService.createBooking(
//            businessId = businessId, bookingReq = booking
//        )
//        return resultHandler{ responseResult }
//    }
//
//
//    suspend fun cancelOrder(bookingId: Int): NetworkResult<BookingRes> {
//        val responseResult = bookingService.cancelByUserBooking(bookingId)
//        return resultHandler{ responseResult }
//    }
