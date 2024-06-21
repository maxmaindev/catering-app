package com.example.cateringapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.remote.datasources.UserRemoteDataSource
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.res.UserInfoRes
import com.example.cateringapp.remote.res.UserRole
import com.example.cateringapp.remote.utils.checkNetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userRemote: UserRemoteDataSource,
) : ViewModel() {

    private val _currentOrder = MutableLiveData<Unit>()
    val currentOrder: LiveData<Unit> = _currentOrder

    private val _currentBusiness = MutableLiveData<BusinessRes?>()
    val currentBusiness: LiveData<BusinessRes?> = _currentBusiness

    private val _currentUser = MutableLiveData<UserInfoRes?>()
    val currentUser: LiveData<UserInfoRes?> = _currentUser

    private val _isUnauthorized = MutableLiveData<Boolean?>()
    val isUnauthorized: LiveData<Boolean?> = _isUnauthorized

    fun setCurrentBis(business: BusinessRes) {
        _currentBusiness.value = business
    }

    private fun setCurrentUser(user: UserInfoRes) {
        viewModelScope.launch(Dispatchers.Main) {
            _currentUser.value = user
        }
    }

    private fun setIsUnauthorized(isUnauthorized: Boolean) {
        viewModelScope.launch(Dispatchers.Main) {
            _isUnauthorized.value = isUnauthorized
        }
    }


    fun loadCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = userRemote.getMyInfo()
            checkNetworkResult(
                res = res,
                onSuccess = { successRes -> setCurrentUser(successRes.data) },
                onUnauthorized = { setIsUnauthorized(true) },//TODO: ACTIVITY OBSERVE UNAUTH
                onOtherState = { Log.e("SharedViewModel","onOtherState") }
            )
        }
    }
}