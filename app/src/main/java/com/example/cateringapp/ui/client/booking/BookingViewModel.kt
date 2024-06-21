package com.example.cateringapp.ui.client.booking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.BookingRemoteDataSource
import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRemote: BookingRemoteDataSource,
    private val prefsRepos: UserPreferencesRepository,
) : ViewModel() {

    private val _userBooking = MutableLiveData<NetworkResult<List<BookingRes>>>()
    val userBooking: LiveData<NetworkResult<List<BookingRes>>> = _userBooking

    private val _userActiveBooking = MutableLiveData<NetworkResult<List<BookingRes>>>()
    val userActiveBooking: LiveData<NetworkResult<List<BookingRes>>> = _userActiveBooking

    private val _userHistoryBooking = MutableLiveData<NetworkResult<List<BookingRes>>>()
    val userHistoryBooking: LiveData<NetworkResult<List<BookingRes>>> = _userHistoryBooking

    fun loadUserBookings(ignoreCancelled: Boolean = true) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = bookingRemote.getUserBookings()
            withContext(Dispatchers.Main) {
                _userBooking.value = res
            }
        }
    }

    fun loadActivityUserBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = bookingRemote.getActivityUserBookings()
            withContext(Dispatchers.Main) {
                _userActiveBooking.value = res
            }
        }
    }

    fun loadHistoryUserBookings() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = bookingRemote.getUserHistoryBookings()
            withContext(Dispatchers.Main) {
                _userHistoryBooking.value = res
            }
        }
    }

    suspend fun createUserBookings(
        businessId: Int,
        booking: BookingReq,
    ): NetworkResult<BookingRes> {
        return bookingRemote.createUserBookings(businessId, booking)
    }

    suspend fun cancelBooking(bookingId: Int): NetworkResult<BookingRes> {
        return bookingRemote.cancelBooking(bookingId)
    }
}