package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.BusinessEditReq
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.services.BusinessService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler
import okhttp3.MultipartBody


class BusinessRemoteDataSource(
    private val businessService: BusinessService,
) {

    suspend fun getAll(): NetworkResult<List<BusinessRes>> {
        val responseResult = businessService.getAll()
        return resultHandler { responseResult }
    }

    suspend fun getBusinessId(businessId: Int): NetworkResult<BusinessRes> {
        val responseResult = businessService.getById(businessId)
        return resultHandler { responseResult }
    }

    suspend fun getUserBusinesses(): NetworkResult<List<BusinessRes>> {
        val responseResult = businessService.getUserBusinesses()
        return resultHandler { responseResult }
    }

    suspend fun updateBusiness(
        businessId: Int,
        item: BusinessEditReq,
    ): NetworkResult<BusinessRes> {
        val responseResult = businessService.update(
            businessId = businessId, businessData = item
        )
        return resultHandler { responseResult }
    }

    suspend fun createBusiness(
        item: BusinessEditReq,
    ): NetworkResult<BusinessRes> {
        val responseResult = businessService.create(businessData = item)
        return resultHandler { responseResult }
    }

    suspend fun uploadBusinessImg(
        businessId: Int,
        businessImage: MultipartBody.Part,
    ): NetworkResult<Unit> {
        val responseResult = businessService.uploadBusinessImage(
            businessId = businessId, businessImage = businessImage
        )
        return resultHandler { responseResult }
    }


}
