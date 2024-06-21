package com.example.cateringapp.ui.client.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.ui.adapters.OrderHistoryAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderHistoryFragment() : Fragment(), OrderHistoryAdapter.OnItemClickListener {
    private val ordersVM: OrdersViewModel by activityViewModels()

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
        val itemAdapter = OrderHistoryAdapter(this)
        binding.itemRv.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initFragment() {
        ordersVM.loadUserOrderHistory()
        ordersVM.orderHistory.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    onOrderHistorySuccess(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    ordersVM.loadUserOrderHistory()
                })
        }
    }

    private fun onOrderHistorySuccess(data: List<OrderRes>) {
        (binding.itemRv.adapter as OrderHistoryAdapter).submitList(data)
    }


    override fun onItemClick(order: OrderRes) {
        val dir = OrdersFragmentDirections.actionNavigationOrdersToNavigationOrderDetails(order.id)
        findNavController().navigate(dir)
    }

}