package com.example.cateringapp.ui.client.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.core.models.CurrentOrder
import com.example.cateringapp.core.models.InitialOrderItem
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.core.models.toRequest
import com.example.cateringapp.remote.datasources.OrderRemoteDataSource
import com.example.cateringapp.remote.req.OrderItemReq
import com.example.cateringapp.remote.req.OrderReq
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.res.OrderStatus
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRemote: OrderRemoteDataSource,
    private val prefsRepos: UserPreferencesRepository,
) : ViewModel() {

    private val _orderHistoryRes = MutableLiveData<NetworkResult<List<OrderRes>>>()
    val orderHistory: LiveData<NetworkResult<List<OrderRes>>> = _orderHistoryRes

    private val _orderBasket = MutableLiveData<List<OrderItem>>()
    val orderBasket: LiveData<List<OrderItem>> = _orderBasket

    private val _currentOrder = MutableLiveData<CurrentOrder>()
    val currentOrderLV: LiveData<CurrentOrder> = _currentOrder

    var currentOrder: CurrentOrder = CurrentOrder()

    fun loadBasket() = viewModelScope.launch(Dispatchers.IO) {
        val res = orderRemote.getUserBasket()
        withContext(Dispatchers.Main) {
            if (res is NetworkResult.Success) {
                currentOrder.setRemote(res.data)
                currentOrder.askedForPayment = res.data.status == OrderStatus.AwaitingPayments
                _orderBasket.value = res.data.items!!.map { OrderItem(it.count, it.menuItem) }
            } else if (res is NetworkResult.HttpError && (res.code == HttpURLConnection.HTTP_NOT_FOUND)) {
                updateOrderList(remoteOrderEmpty = true)
            }
        }

    }


    fun loadUserOrderHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val res = orderRemote.getUserOrdersHistory()
            withContext(Dispatchers.Main) {
                _orderHistoryRes.value = res
            }
        }
    }

    fun getOrderFromListById(orderId: Int): OrderRes? {
        if (orderHistory.value is NetworkResult.Success) {
            val res = (orderHistory.value as NetworkResult.Success<List<OrderRes>>).data
                .first { it.id == orderId }
            return res
        } else return null
    }

    fun getMenuItemOrderInfo(menuItemId: Int): OrderItem? {
        val orderItems = orderBasket.value ?: return null
        val orderItem = orderItems.find { it.menuItem.id == menuItemId }
        return orderItem
    }

    fun createOrderInitial(tableName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("OrdersViewModel", "createOrderInitial: Order is ${currentOrder.isOrderReady()}!")
            if (currentOrder.isOrderReady()) {
                val order = currentOrder
                val res = orderRemote.createOrderInitial(
                    businessId = order.businessId,
                    orderBody = OrderReq(
                        businessId = order.businessId,
                        items = currentOrder.initialItems.toList().toRequest(),
                        info = "Столик $tableName"
                    )
                )
                if (res is NetworkResult.Success) {
                    currentOrderLV.value
                    currentOrder.orderId = res.data.id
                    currentOrder.isLocal = false
                    updateOrderList()
                }
            }
        }
    }

    private fun addOrderItemToRemote(orderId: Int, menuItemId: Int, itemCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = orderRemote.addOrderItem(
                orderId = currentOrder.orderId,
                orderItems = listOf(
                    OrderItemReq(
                        orderId = orderId,
                        menuItemId = menuItemId,
                        count = itemCount
                    )
                )
            )
            if (res is NetworkResult.Success) {
                updateOrderList()
            }
        }
    }

    fun addToOrder(menuItem: MenuItemRes, itemCount: Int) {
        if (currentOrder.businessId != menuItem.businessId) {
            currentOrder.resetOrder()
        }
        if (currentOrder.isLocal) {
            currentOrder.businessId = menuItem.businessId
            if (currentOrder.hasMenuItem(menuItem.id)) {
                currentOrder.addCount(menuItem.id, itemCount)
                updateOrderList()
            } else {
                currentOrder.initialItems.add(InitialOrderItem(menuItem, itemCount))
                updateOrderList()
            }
        } else {
            addOrderItemToRemote(currentOrder.orderId, menuItem.id, itemCount)
        }
    }


    fun updateOrderList(remoteOrderEmpty: Boolean = false) {
        if (remoteOrderEmpty && currentOrder.askedForPayment){
            currentOrder.resetOrder()
        }
        if (currentOrder.isLocal && currentOrder.initialItems.isNotEmpty()) {
            _orderBasket.value = currentOrder.initialItems.map { OrderItem(it.count, it.menuItem) }
        } else if (currentOrder.isLocal && currentOrder.initialItems.isEmpty()) {
            _orderBasket.value = emptyList()
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val res = orderRemote.getUserBasket()
                if (res is NetworkResult.Success) {
                    withContext(Dispatchers.Main) {
                        _orderBasket.value =
                            res.data.items!!.map { OrderItem(it.count, it.menuItem) }
                    }

                }
            }
        }
    }

    suspend fun basketAskForPayment() = orderRemote.basketAskForPayment()

    fun clearOrder() {
        currentOrder = CurrentOrder()
        updateOrderList()
    }

    fun updateLocalCount(orderItem: OrderItem, newCount: Int) {
        if (currentOrder.isLocal) {
            currentOrder.editCount(orderItem.menuItem.id, newCount)
        }
    }


}