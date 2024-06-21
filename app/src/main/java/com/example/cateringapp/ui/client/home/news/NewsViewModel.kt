package com.example.cateringapp.ui.client.home.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.remote.datasources.NewsRemoteDataSource
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRemote: NewsRemoteDataSource,
    private val userPrefs: UserPreferencesRepository,
) : ViewModel() {

    private val _newsRes = MutableLiveData<NetworkResult<List<NewsRes>>>()
    val newsRes: LiveData<NetworkResult<List<NewsRes>>> = _newsRes

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

}