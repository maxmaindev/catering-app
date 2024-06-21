package com.example.cateringapp.ui.client.home.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.BusinessRemoteDataSource
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BusinessesViewModel @Inject constructor(
    private val businessRemote: BusinessRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    private val _businessesRes = MutableLiveData<NetworkResult<List<BusinessRes>>>()
    val businessesRes: LiveData<NetworkResult<List<BusinessRes>>> = _businessesRes

    private val _selectedBusinessRes = MutableLiveData<NetworkResult<BusinessRes>>()
    val selectedBusinessRes: LiveData<NetworkResult<BusinessRes>> = _selectedBusinessRes

    fun loadBusinesses() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = businessRemote.getAll()
            withContext(Dispatchers.Main) {
                _businessesRes.value = res
            }
        }
    }

    fun loadUserBusinesses() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = businessRemote.getUserBusinesses()
            withContext(Dispatchers.Main) {
                _businessesRes.value = res
            }
        }
    }

    fun loadSelectedBusiness() {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = userPrefs.getSelectedBusiness() ?: return@launch
            val res = businessRemote.getBusinessId(businessId)
            withContext(Dispatchers.Main) {
                _selectedBusinessRes.value = res
            }
        }
    }


    fun getBusinessById(businessId: Int): BusinessRes? {
        if (businessesRes.value is NetworkResult.Success) {
            val res = (businessesRes.value as NetworkResult.Success<List<BusinessRes>>).data
                .first { it.id == businessId }
            return res
        } else return null
    }

}
