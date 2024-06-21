package com.example.cateringapp.remote.datasources

import com.example.cateringapp.remote.req.MenuItemReq

import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.services.MenuService
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.remote.utils.resultHandler
import okhttp3.MultipartBody


class MenuRemoteDataSource(
    private val menuService: MenuService
) {

    suspend fun getAll(businessId: Int): NetworkResult<List<MenuItemRes>> {
        val responseResult = menuService.getMenu(businessId)
        return resultHandler{ responseResult }
    }

    suspend fun createMenuItem(
        businessId: Int,
        item: MenuItemReq
    ): NetworkResult<MenuItemRes> {
        val responseResult = menuService.create(
            businessId = businessId, itemData = item
        )
        return resultHandler{ responseResult }
    }

    suspend fun updateMenuItem(
        businessId: Int,
        menuItemId: Int,
        item: MenuItemReq
    ): NetworkResult<MenuItemRes> {
        val responseResult = menuService.update(
            businessId = businessId, menuItemId = menuItemId, itemData = item
        )
        return resultHandler{ responseResult }
    }

    suspend fun removeMenuItem(
        businessId: Int,
        menuItemId: Int
    ): NetworkResult<Unit> {
        val responseResult = menuService.delete(
            businessId = businessId, menuItemId = menuItemId
        )
        return resultHandler{ responseResult }
    }

    suspend fun uploadMenuItemImg(
        businessId: Int,
        menuItemId: Int,
        profileImage: MultipartBody.Part
    ): NetworkResult<Unit> {
        val responseResult = menuService.uploadMenuItemImage(
            businessId = businessId, menuItemId = menuItemId, profileImage = profileImage
        )
        return resultHandler{ responseResult }
    }

}
