package com.example.cateringapp.ui.admin.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.BusinessRemoteDataSource
import com.example.cateringapp.remote.datasources.UserRemoteDataSource
import com.example.cateringapp.remote.req.BusinessEditReq
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.req.UserInfoData
import com.example.cateringapp.remote.req.UserInfoEditData
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.res.UserInfoRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AdminProfileViewModel @Inject constructor(
    private val businessRemote: BusinessRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    suspend fun editBusiness(
        newData: BusinessEditReq,
        selectedImg: MultipartBody.Part?,
    ): NetworkResult<Any> {
        val businessId = userPrefs.getSelectedBusiness() ?: return NetworkResult.SuccessNullBody
        val resDataUpdate = businessRemote.updateBusiness(businessId, newData)
        if ((selectedImg != null) && (resDataUpdate is NetworkResult.Success)) {
            val resImgUpdate = businessRemote.uploadBusinessImg(
                businessId = businessId,
                businessImage = selectedImg
            )
            if (resImgUpdate is NetworkResult.Success) {
                return resImgUpdate
            }
        }
        return resDataUpdate
    }

    suspend fun newBusiness(
        newData: BusinessEditReq,
        selectedImg: MultipartBody.Part?,
    ): NetworkResult<BusinessRes> {
        val resDataUpdate = businessRemote.createBusiness(newData)
        if ((selectedImg != null) && (resDataUpdate is NetworkResult.Success)) {
            val businessId = resDataUpdate.data.id
            val resImgUpdate = businessRemote.uploadBusinessImg(
                businessId = businessId,
                businessImage = selectedImg
            )
        }
        return resDataUpdate

    }
}

