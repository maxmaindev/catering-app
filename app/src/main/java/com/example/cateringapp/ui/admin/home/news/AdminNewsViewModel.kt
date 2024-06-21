package com.example.cateringapp.ui.admin.home.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.NewsRemoteDataSource
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.req.NewsEditReq
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AdminNewsViewModel @Inject constructor(
    private val newsRemote: NewsRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    private val _newsRes = MutableLiveData<NetworkResult<List<NewsRes>>>()
    val newsRes: LiveData<NetworkResult<List<NewsRes>>> = _newsRes

    private val _addNewsRes = MutableLiveData<NetworkResult<NewsRes>>()
    val addNewsRes: LiveData<NetworkResult<NewsRes>> = _addNewsRes

    private val _editNewsRes = MutableLiveData<NetworkResult<NewsRes>>()
    val editNewsRes: LiveData<NetworkResult<NewsRes>> = _editNewsRes

    fun loadNews(businessId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = newsRemote.getNewsByBusiness(businessId)
            withContext(Dispatchers.Main) {
                _newsRes.value = res
            }
        }
    }

    fun getItemById(newsItemId: Int): NewsRes? {
        if (newsRes.value is NetworkResult.Success) {
            val res = (newsRes.value as NetworkResult.Success<List<NewsRes>>).data
                .first { it.id == newsItemId }
            return res
        } else return null
    }


    fun editItem(newsId: Int, newData: NewsEditReq, selectedImg: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = getSelectedBusiness() ?: return@launch
            val resDataUpdate = newsRemote.updateNewsItem(newsId, newData)
            if ((selectedImg != null)) {
                val resImgUpdate = newsRemote.uploadNewsItemImg(
                    newsId = newsId,
                    newsImage = selectedImg
                )
            }
            withContext(Dispatchers.Main){
                _editNewsRes.value = resDataUpdate
            }

        }
    }


    fun newItem(newData: NewsEditReq, selectedImg: MultipartBody.Part?) {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = getSelectedBusiness() ?: return@launch
            val res = newsRemote.createNewsItem(businessId, newData)
            if ((res is NetworkResult.Success) && (selectedImg != null)) {
                newsRemote.uploadNewsItemImg(
                    newsId = res.data.id,
                    newsImage = selectedImg
                )
            }
            withContext(Dispatchers.Main){
                _addNewsRes.value = res
            }

        }
    }

    suspend fun getSelectedBusiness(): Int? {
        return userPrefs.getSelectedBusiness()
    }

    suspend fun removeItem(newsId: Int): NetworkResult<Unit> {
        return newsRemote.removeNewsItem(
            newsId = newsId
        )

    }

}