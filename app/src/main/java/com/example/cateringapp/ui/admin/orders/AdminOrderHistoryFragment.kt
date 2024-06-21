package com.example.cateringapp.ui.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.res.OrderStatus
import com.example.cateringapp.ui.adapters.AdminOrderAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneOffset

@AndroidEntryPoint
class AdminOrderHistoryFragment() : Fragment(), AdminOrderAdapter.OnItemClickListener {
    private val ordersVM: AdminOrdersViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRvBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initFragment()
    }


    private fun initRv() {
        val itemAdapter = AdminOrderAdapter(this)
        binding.itemRv.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initFragment() {
        ordersVM.loadHistoryOrders()
        ordersVM.ordersHistoryRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    onOrderHistorySuccess(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    ordersVM.loadHistoryOrders()

                })
        }

        ordersVM.updateOrderRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = {ordersVM.loadHistoryOrders()},
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    ordersVM.loadHistoryOrders()

                })
        }
    }

    private fun onOrderHistorySuccess(data: List<OrderRes>) {
        val items = data.sortedByDescending { it.date.toEpochSecond(ZoneOffset.UTC)}
        (binding.itemRv.adapter as AdminOrderAdapter).submitList(items)
    }

    override fun onSpinnerClick(order: OrderRes, status: OrderStatus) {
        ordersVM.updateOrderStatus(order, status)
    }

    override fun onItemClick(order: OrderRes) {
        ordersVM.setSelectedOrder(order)
        val dir = AdminOrdersFragmentDirections
            .actionNavigationAdminOrdersToNavigationAdminOrderDetails(order.id)
        findNavController().navigate(dir)
    }

}