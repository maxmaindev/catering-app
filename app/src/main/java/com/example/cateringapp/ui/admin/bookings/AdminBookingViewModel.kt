package com.example.cateringapp.ui.admin.bookings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.BookingRemoteDataSource
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.BookingStatus
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminBookingViewModel @Inject constructor(
    private val bookingRemote: BookingRemoteDataSource,
    private val prefsRepos: UserPreferencesRepository,
) : ViewModel() {


    private val _historyBusinessBooking = MutableLiveData<NetworkResult<List<BookingRes>>>()
    val historyBusinessBooking: LiveData<NetworkResult<List<BookingRes>>> = _historyBusinessBooking

    private val _updateBooking = MutableLiveData<NetworkResult<Unit>>()
    val updateBooking: LiveData<NetworkResult<Unit>> = _updateBooking

    private val _activeBusinessBooking = MutableLiveData<NetworkResult<List<BookingRes>>>()
    val activeBusinessBooking: LiveData<NetworkResult<List<BookingRes>>> = _activeBusinessBooking


    fun loadActiveBusinessBookings(){
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = prefsRepos.getSelectedBusiness() ?: return@launch
            val res = bookingRemote.getActiveBusinessBookings(businessId)
            withContext(Dispatchers.Main){
                _activeBusinessBooking.value = res
            }
        }
    }

    fun loadFinishedBusinessBookings(){
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = prefsRepos.getSelectedBusiness() ?: return@launch
            val res = bookingRemote.getBusinessHistoryBookings(businessId)
            withContext(Dispatchers.Main){
                _historyBusinessBooking.value = res
            }
        }
    }

    fun updateBookingStatus(booking: BookingRes, status: BookingStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = bookingRemote.updateStatusOrder(booking.id, StatusReq(status.status))
            withContext(Dispatchers.Main){
                _updateBooking.value = res
            }
        }
    }
}