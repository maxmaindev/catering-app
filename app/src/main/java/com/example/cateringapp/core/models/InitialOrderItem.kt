package com.example.cateringapp.core.models

import com.example.cateringapp.remote.req.InitialOrderItemReq
import com.example.cateringapp.remote.res.MenuItemRes
import kotlinx.serialization.Serializable

@Serializable
data class InitialOrderItem(
    val menuItem: MenuItemRes,
    var count: Int
)

fun List<InitialOrderItem>.toRequest(): List<InitialOrderItemReq> {
    val initialOrderItemsReq = this.map { InitialOrderItemReq(it.menuItem.id, it.count) }
    return initialOrderItemsReq
}