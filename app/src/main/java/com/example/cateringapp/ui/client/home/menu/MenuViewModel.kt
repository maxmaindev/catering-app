package com.example.cateringapp.ui.client.home.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.MenuRemoteDataSource
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRemote: MenuRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    private val _menuRes = MutableLiveData<NetworkResult<List<MenuItemRes>>>()
    val menuRes: LiveData<NetworkResult<List<MenuItemRes>>> = _menuRes

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

}