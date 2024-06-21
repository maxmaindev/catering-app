package com.example.cateringapp.ui.client.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.remote.datasources.UserRemoteDataSource
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.req.UserInfoEditData
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.res.UserInfoRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRemote: UserRemoteDataSource,
) : ViewModel() {

    private val _userInfo = MutableLiveData<NetworkResult<UserInfoRes>>()
    val userInfo: LiveData<NetworkResult<UserInfoRes>> = _userInfo


    suspend fun updateUserInfo(
        name: String,
        surname: String,
        email: String,
        phone: String,
    ): NetworkResult<UserInfoRes> {
        val res = userRemote.updateUserInfo(
            UserInfoEditData(
                name = name,
                surname = surname,
                email = email,
                phone = phone
            )
        )
        withContext(Dispatchers.Main) {
            _userInfo.value = res
        }
        return res
    }


    fun getMyUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = userRemote.getMyInfo()
            withContext(Dispatchers.Main) {
                _userInfo.value = res
            }
        }
    }

}