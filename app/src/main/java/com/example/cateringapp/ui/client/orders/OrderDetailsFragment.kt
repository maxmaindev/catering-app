package com.example.cateringapp.ui.client.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.ui.adapters.OrderItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailsFragment() : Fragment() {
    private val ordersVM: OrdersViewModel by activityViewModels()
    private val sharedVM: SharedViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    private val binding get() = _binding!!
    val args: OrderDetailsFragmentArgs by navArgs()

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
        val itemAdapter = OrderItemAdapter()
        binding.itemRv.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initFragment() {
        val res = ordersVM.getOrderFromListById(args.orderId) ?: return
        onBasketSuccess(res)

    }

    private fun onBasketSuccess(data: OrderRes) {
        val menuItems = data.items!!.map { OrderItem(it.count,it.menuItem) }
        (binding.itemRv.adapter as OrderItemAdapter).submitList(menuItems)

    }

}