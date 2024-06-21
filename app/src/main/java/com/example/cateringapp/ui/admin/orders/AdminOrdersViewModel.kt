package com.example.cateringapp.ui.admin.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cateringapp.core.UserPreferencesRepository
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.core.models.toRequest
import com.example.cateringapp.remote.datasources.OrderRemoteDataSource
import com.example.cateringapp.remote.req.OrderItemReq
import com.example.cateringapp.remote.req.OrderReq
import com.example.cateringapp.remote.req.StatusReq
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.res.OrderStatus
import com.example.cateringapp.remote.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository,
    private val orderRemote: OrderRemoteDataSource,
) : ViewModel() {

    private val _ordersActiveRes = MutableLiveData<NetworkResult<List<OrderRes>>>()
    val ordersActiveRes: LiveData<NetworkResult<List<OrderRes>>> = _ordersActiveRes

    private val _ordersHistoryRes = MutableLiveData<NetworkResult<List<OrderRes>>>()
    val ordersHistoryRes: LiveData<NetworkResult<List<OrderRes>>> = _ordersHistoryRes

    private val _selectedOrderRes = MutableLiveData<OrderRes>()
    val selectedOrderRes: LiveData<OrderRes> = _selectedOrderRes

    private val _updateOrderRes = MutableLiveData<NetworkResult<Unit>>()
    val updateOrderRes: LiveData<NetworkResult<Unit>> = _updateOrderRes

    private val _servedOrderRes = MutableLiveData<NetworkResult<Unit>>()
    val servedOrderRes: LiveData<NetworkResult<Unit>> = _servedOrderRes

    fun createOrderManual(tableName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = prefs.getSelectedBusiness() ?: return@launch

            val res = orderRemote.createOrderManual(
                businessId = businessId,
                orderBody = OrderReq(
                    businessId = businessId,
                    items = emptyList(),
                    info = "Столик $tableName"
                )
            )
            if (res is NetworkResult.Success) {
                loadActiveOrders()
            }

        }
    }

    fun loadSelectedOrder(){
        viewModelScope.launch(Dispatchers.IO){
            val orderId = selectedOrderRes.value?.id ?: return@launch
            val res = orderRemote.getOrderItemByID(orderId)
            withContext(Dispatchers.Main) {
                if (res is NetworkResult.Success) {
                    _selectedOrderRes.value = res.data
                }
            }
        }
    }


    fun loadActiveOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = prefs.getSelectedBusiness() ?: return@launch
            val res = orderRemote.getBusinessOrderActive(businessId)
            withContext(Dispatchers.Main) {
                _ordersActiveRes.value = res
            }
        }
    }

    fun loadHistoryOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            val businessId = prefs.getSelectedBusiness() ?: return@launch
            val res = orderRemote.getBusinessOrderHistory(businessId)
            withContext(Dispatchers.Main) {
                _ordersHistoryRes.value = res
            }
        }
    }

    fun setSelectedOrder(orderRes: OrderRes) {
        _selectedOrderRes.value = orderRes
    }

    fun updateOrderStatus(order: OrderRes, status: OrderStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = orderRemote.updateOrderStatus(
                businessId = order.businessId,
                orderId = order.id,
                statusBody = StatusReq(status.status)
            )
            withContext(Dispatchers.Main) {
                _updateOrderRes.value = res
            }
        }
    }

    fun updateOrderItemStatus(isServed: Boolean, orderId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val res = orderRemote.updateOrderItemServedStatus(
                orderId = orderId,
                isServed = isServed
            )
            withContext(Dispatchers.Main) {
                _servedOrderRes.value = res
            }
        }
    }

    suspend fun addItemToRemote(orderId: Int, menuItemId: Int, count: Int): NetworkResult<*> {
        return orderRemote.addOrderItem(
            orderId = orderId,
            listOf(
                OrderItemReq(
                    orderId = orderId,
                    menuItemId = menuItemId,
                    count = count
                )
            )
        )
    }


}