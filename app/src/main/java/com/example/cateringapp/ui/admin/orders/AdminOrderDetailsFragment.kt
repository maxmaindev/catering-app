package com.example.cateringapp.ui.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.databinding.FragmentRvTextOnTopBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.ui.adapters.OrderItemAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminOrderDetailsFragment() : Fragment() {
    private val ordersVM: AdminOrdersViewModel by activityViewModels()

    private var _binding: FragmentRvTextOnTopBinding? = null
    private val binding get() = _binding!!
    val args: AdminOrderDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRvTextOnTopBinding.inflate(inflater, container, false)
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

        binding.btnAdd.setOnClickListener {
            val dir = AdminOrderDetailsFragmentDirections.
            actionNavigationAdminOrderDetailsToNavigationAdminOrderAdd(args.orderId)
            findNavController().navigate(dir)
        }
    }

    private fun initFragment() {

        ordersVM.selectedOrderRes.observe(viewLifecycleOwner) { res ->
            val items = res.items?.map { OrderItem(it.count, it.menuItem) }
                ?: return@observe
            var data = res
            binding.apply {
                tvInfoValue.text = data.info
                tvCustomerNameSurname.text = "${data.user?.name} ${data.user?.surname}"
                val total = items.sumOf { (it.count * it.menuItem.price).toInt() }.toString() + " грн"
                tvTotalValue.text = total
            }
            (binding.itemRv.adapter as OrderItemAdapter).submitList(items)

        }
        ordersVM.loadSelectedOrder()


    }

    private fun onOrderGetSuccess(data: OrderRes) {

        val items = data.items!!.map { OrderItem(it.count, it.menuItem) }
        binding.apply {
            tvInfoValue.text = data.info
            tvCustomerNameSurname.text = "${data.user?.name} ${data.user?.surname}"
            val total = data.items.sumOf { (it.count * it.menuItem.price).toInt() }.toString() + " грн"
            tvTotalValue.text = total
        }
        (binding.itemRv.adapter as OrderItemAdapter).submitList(items)

    }


}