package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.NewsEditReq
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.remote.services.NewsService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler
import okhttp3.MultipartBody


class NewsRemoteDataSource(
    private val newsService: NewsService,
) {


    suspend fun getNewsByBusiness(newsId: Int): NetworkResult<List<NewsRes>> {
        val responseResult = newsService.getNews(newsId)
        return resultHandler{ responseResult }
    }

    suspend fun createNewsItem(
        businessId: Int,
        item: NewsEditReq
    ): NetworkResult<NewsRes> {
        val responseResult = newsService.create(
            businessId = businessId, itemData = item
        )
        return resultHandler{ responseResult }
    }

    suspend fun updateNewsItem(
        newsId: Int,
        item: NewsEditReq
    ): NetworkResult<NewsRes> {
        val responseResult = newsService.update(
            newsId = newsId, itemData = item
        )
        return resultHandler{ responseResult }
    }

    suspend fun removeNewsItem(
        newsId: Int,
    ): NetworkResult<Unit> {
        val responseResult = newsService.delete(
            newsId = newsId
        )
        return resultHandler{ responseResult }
    }

    suspend fun uploadNewsItemImg(
        newsId: Int,
        newsImage: MultipartBody.Part
    ): NetworkResult<Unit> {
        val responseResult = newsService.uploadNewsItemImage(
            newsId = newsId, newsImage = newsImage
        )
        return resultHandler{ responseResult }
    }


}
