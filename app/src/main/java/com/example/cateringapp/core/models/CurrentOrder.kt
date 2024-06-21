package com.example.cateringapp.core.models

import com.example.cateringapp.remote.req.InitialOrderItemReq
import com.example.cateringapp.remote.res.OrderRes

data class CurrentOrder(
    var businessId: Int = -1,
    var orderId: Int = -1,
    val initialItems: MutableList<InitialOrderItem> = mutableListOf(),
    var isLocal: Boolean = true,
    var askedForPayment :Boolean = false
) {
    fun resetOrder() {
        businessId = -1
        initialItems.clear()
        isLocal = true
    }

    fun isOrderReady(): Boolean {
        return ((businessId != -1) && initialItems.isNotEmpty() && isLocal)
    }

    fun setRemote(order: OrderRes) {
        businessId = order.businessId
        orderId = order.id
        isLocal = false
    }

    fun hasMenuItem(menuItemId: Int): Boolean {
        val isFound = (initialItems.find { it.menuItem.id == menuItemId } != null)
        return isFound
    }

    fun addCount(menuItemId: Int, count: Int) {
        val foundItemIndex = initialItems.indexOfFirst { it.menuItem.id == menuItemId }
        if (foundItemIndex == -1) return
        initialItems[foundItemIndex].count += count
    }

    fun editCount(menuItemId: Int, newCount: Int){
        val foundItemIndex = initialItems.indexOfFirst { it.menuItem.id == menuItemId }
        if (foundItemIndex == -1) return
        if (newCount > 0)
            initialItems[foundItemIndex].count = newCount
    }


}