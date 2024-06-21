package com.example.cateringapp.ui.admin.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.databinding.FragmentRvBottomFabBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.res.OrderStatus
import com.example.cateringapp.ui.adapters.AdminOrderAdapter
import com.example.cateringapp.ui.adapters.OrderHistoryAdapter
import com.example.cateringapp.ui.client.orders.AdditionInfoDialogFragment
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.setInvisible
import com.example.cateringapp.utils.setVisible
import com.example.cateringapp.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ActiveAdminOrdersFragment() : Fragment(), OrderHistoryAdapter.OnItemClickListener,
    AdminOrderAdapter.OnItemClickListener {

    private val ordersVM: AdminOrdersViewModel by activityViewModels()

    private var _binding: FragmentRvBottomFabBinding? = null
    private val binding get() = _binding!!

    private var autoUpdateEnabled: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRvBottomFabBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRv()
        initFab()
        initFragment()
        startUpdating()
        setupRefresh()
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            val dialog = AdditionInfoDialogFragment { tableName ->
                if (tableName.isNullOrEmpty()) {
                    shortToast(requireContext(), getString(R.string.you_didnt_write_the_table))
                }
                ordersVM.createOrderManual(tableName)
            }
            dialog.show(parentFragmentManager, "TableNameDialogFragment")


        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            ordersVM.loadActiveOrders()
        }
    }

    private fun startUpdating() {
        ordersVM.loadActiveOrders()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            while (autoUpdateEnabled) {
                delay(10000) // Update every 10 second
                ordersVM.loadActiveOrders()
            }
        }
    }


    private fun initRv() {
        val itemAdapter = AdminOrderAdapter(this)
        binding.itemRv.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initFragment() {
        ordersVM.ordersActiveRes.observe(viewLifecycleOwner) { res ->
            binding.swipeRefresh.isRefreshing = false

            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    if (data.isEmpty())
                        binding.tvEmpty.setVisible()
                    else binding.tvEmpty.setInvisible()

                    onOrderHistorySuccess(data)
                    autoUpdateEnabled = true
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                onFailure = {
                    autoUpdateEnabled = false
                },
                retry = {
                    ordersVM.loadActiveOrders()

                })
        }
        ordersVM.updateOrderRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { ordersVM.loadActiveOrders() },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                onFailure = {
                    autoUpdateEnabled = false
                },
                retry = {
                    ordersVM.loadActiveOrders()

                })
        }
    }

    private fun onOrderHistorySuccess(data: List<OrderRes>) {
        (binding.itemRv.adapter as AdminOrderAdapter).submitList(data)
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