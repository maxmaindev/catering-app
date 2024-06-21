package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.services.BookingService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler

class BookingRemoteDataSource(
    private val bookingService: BookingService
) {

    suspend fun getUserBookings(): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getUserBookings()
        return resultHandler{ responseResult }
    }

    suspend fun getActivityUserBookings(): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getActiveUserBookings()
        return resultHandler{ responseResult }
    }

    suspend fun getUserHistoryBookings(): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getHistoryUserBookings()
        return resultHandler{ responseResult }
    }

    suspend fun getActiveBusinessBookings(businessId: Int): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getActiveBusinessBookings(businessId)
        return resultHandler{ responseResult }
    }

    suspend fun getBusinessHistoryBookings(businessId: Int): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getHistoryBusinessBookings(businessId)
        return resultHandler{ responseResult }
    }

    suspend fun getBusinessBooking(
        businessId: Int,
    ): NetworkResult<List<BookingRes>> {
        val responseResult = bookingService.getBusinessBookings(businessId)
        return resultHandler{ responseResult }
    }

    suspend fun createUserBookings(
        businessId: Int,
        booking: BookingReq
    ): NetworkResult<BookingRes> {
        val responseResult = bookingService.createBooking(
            businessId = businessId, bookingReq = booking
        )
        return resultHandler{ responseResult }
    }
    suspend fun updateStatusOrder(bookingId: Int, statusReq: StatusReq): NetworkResult<Unit> {
        val responseResult = bookingService.updateStatusBooking(bookingId,statusReq)
        return resultHandler{ responseResult }
    }

    suspend fun cancelBooking(bookingId: Int): NetworkResult<BookingRes> {
        val responseResult = bookingService.cancelByUserBooking(bookingId)
        return resultHandler{ responseResult }
    }
}