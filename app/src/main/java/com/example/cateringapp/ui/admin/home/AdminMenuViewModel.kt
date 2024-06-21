package com.example.cateringapp.ui.admin.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.MenuRemoteDataSource
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AdminMenuViewModel @Inject constructor(
    private val menuRemote: MenuRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    private val _menuRes = MutableLiveData<NetworkResult<List<MenuItemRes>>>()
    val menuRes: LiveData<NetworkResult<List<MenuItemRes>>> = _menuRes

    private val _newItemRes = MutableLiveData<NetworkResult<MenuItemRes>>()
    val newItemRes: LiveData<NetworkResult<MenuItemRes>> = _newItemRes

    private val _editItemRes = MutableLiveData<NetworkResult<MenuItemRes>>()
    val editItemRes: LiveData<NetworkResult<MenuItemRes>> = _editItemRes


    fun loadMenu(businessId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = menuRemote.getAll(businessId)
            withContext(Dispatchers.Main) {
                _menuRes.value = res
            }
        }
    }

    fun getItemById(menuItemId: Int): MenuItemRes? {
        if (menuRes.value is NetworkResult.Success) {
            val res = (menuRes.value as NetworkResult.Success<List<MenuItemRes>>).data
                .first { it.id == menuItemId }
            return res
        } else return null
    }


    fun editItem(menuItemId: Int, newData: MenuItemReq, selectedImg: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = getSelectedBusiness() ?: return@launch
            val resDataUpdate = menuRemote.updateMenuItem(menuItemId,businessId, newData)
            if ((selectedImg != null)) {
                val resImgUpdate = menuRemote.uploadMenuItemImg(
                    businessId = businessId,
                    menuItemId = menuItemId,
                    profileImage = selectedImg
                )
            }
            withContext(Dispatchers.Main){
                _newItemRes.value = resDataUpdate
            }

        }
    }


    fun newItem(newData: MenuItemReq, selectedImg: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = getSelectedBusiness() ?: return@launch
            val res = menuRemote.createMenuItem(businessId, newData)
            if ((res is NetworkResult.Success) && (selectedImg != null)) {
                menuRemote.uploadMenuItemImg(
                    businessId = businessId,
                    menuItemId = res.data.id,
                    profileImage = selectedImg
                )
            }
            withContext(Dispatchers.Main){
                _newItemRes.value = res
            }

        }
    }

    suspend fun getSelectedBusiness(): Int? {
        return userPrefs.getSelectedBusiness()
    }

    suspend fun removeItem(menuItem: MenuItemRes): NetworkResult<Unit> {
           return menuRemote.removeMenuItem(
                businessId = menuItem.businessId,
                menuItemId = menuItem.id
            )

    }

    fun loadMenu() {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = getSelectedBusiness() ?: return@launch
            loadMenu(businessId)

        }
    }
}