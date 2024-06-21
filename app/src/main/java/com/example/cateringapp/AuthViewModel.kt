package com.example.cateringapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.core.UserRolePrefs
import com.example.cateringapp.remote.datasources.AuthRemoteDataSource
import com.example.cateringapp.remote.req.LoginData
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRemote: AuthRemoteDataSource,
    private val prefsRepos: UserPreferencesRepository,
) : ViewModel() {

    private val _authActionResult = MutableLiveData<NetworkResult<AuthData>>()
    val authActionResult: LiveData<NetworkResult<AuthData>> = _authActionResult

    private val _authTokenResult = MutableLiveData<AuthTokenResult>(AuthTokenResult.Idle)
    val authTokenResult: LiveData<AuthTokenResult> = _authTokenResult

    fun login(email: String, pwd: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = authRemote.login(LoginData(email = email, password = pwd))
            checkAuthResult(res)
        }
    }

    fun register(name: String, surname: String, email: String, pwd: String, phone: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = authRemote.register(
                UserInfoData(
                    name = name,
                    surname = surname,
                    email = email,
                    password = pwd,
                    phone = phone
                )
            )
            checkAuthResult(res)
        }
    }

    private suspend fun saveAccessToken(accessToken: String) {
        prefsRepos.updateToken(accessToken)
    }
    private suspend fun saveUserRole(role: UserRolePrefs) {
        prefsRepos.setRole(role)
    }

    suspend fun saveSelectedBusiness(business: BusinessRes) {
        prefsRepos.setSelectedBusiness(business.id)
    }

    suspend fun saveSelectedBusiness(businessID: Int) {
        prefsRepos.setSelectedBusiness(businessID)
    }

    private suspend fun checkAuthResult(res: NetworkResult<AuthData>) {
        when (res) {
            is NetworkResult.Success -> {
                saveAccessToken(res.data.accessToken)
                saveUserRole(res.data.getPrefsRole())
                setAuthActionRes(res)
            } else -> {
                setAuthActionRes(res)
            }
        }
    }

    private suspend fun setAuthActionRes(res:  NetworkResult<AuthData>) {
        withContext(Dispatchers.Main) {
            _authActionResult.value = res
        }
    }

    fun checkTokenValidation() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = prefsRepos.userTokenFlow.firstOrNull()
            //TODO check token timeout
            withContext(Dispatchers.Main) {
                if (token.isNullOrBlank()) {
                    _authTokenResult.value = AuthTokenResult.StayOnLogin
                } else {
                    _authTokenResult.value = AuthTokenResult.NavigateToMain
                }
            }

        }
    }

    suspend fun getUserRole(): String? {
        return prefsRepos.getRole()
    }

    suspend fun destroyPrefs() {
        val job = viewModelScope.launch(Dispatchers.IO) {
            prefsRepos.resetPrefs()
        }
        _authTokenResult.value = AuthTokenResult.StayOnLogin
        job.join()
    }

}

sealed class AuthTokenResult {
    data object Idle : AuthTokenResult()
    data object StayOnLogin : AuthTokenResult()
    data object NavigateToMain : AuthTokenResult()
}