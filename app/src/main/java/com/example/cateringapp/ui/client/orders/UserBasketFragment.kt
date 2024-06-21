package com.example.cateringapp.ui.client.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.databinding.FragmentRvBottomButtonBinding
import com.example.cateringapp.ui.adapters.OrderItemEditAdapter
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class UserBasketFragment() : Fragment(), OrderItemEditAdapter.OnItemClickListener {
    private val ordersVM: OrdersViewModel by activityViewModels()

    private var _binding: FragmentRvBottomButtonBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRvBottomButtonBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
        observeBasket()
        setupRefresh()
    }


    private fun initFragment() {
        binding.swipeRefresh.isRefreshing = false

        initRv()
        initBottomButton()
        binding.bottomBtn.setOnClickListener {
            onBottomBtnClick()
        }
        binding.clearBtn.setOnClickListener {
            ordersVM.clearOrder()

        }
    }

    private fun onBottomBtnClick() {
        val curOrder = ordersVM.currentOrder
        if (!curOrder.askedForPayment && !curOrder.isLocal) {
            askForPayment()
            return
        }
        createOrder()
    }

    private fun createOrder() {
        val dialog = AdditionInfoDialogFragment { tableName ->
            if (tableName.isNullOrEmpty()) {
                shortToast(requireContext(), getString(R.string.you_didnt_write_the_table))
            }
            ordersVM.createOrderInitial(tableName)
            binding.bottomBtn.visibility = View.INVISIBLE
            binding.bottomBtn.text = getString(R.string.ask_for_payment)
            binding.bottomBtn.visibility = View.VISIBLE
            binding.clearBtn.visibility = View.INVISIBLE

        }
        dialog.show(parentFragmentManager, "TableNameDialogFragment")

    }

    private fun initBottomButton() {
        val currentOrder = ordersVM.currentOrder
        if (currentOrder.isLocal && currentOrder.initialItems.isEmpty()) {
            binding.bottomBtn.visibility = View.INVISIBLE
            binding.tvStatus.text = getString(R.string.your_order_is_empty)

        } else if (!currentOrder.isLocal && !ordersVM.orderBasket.value.isNullOrEmpty()) {
            binding.clearBtn.visibility = View.INVISIBLE

            if (!currentOrder.askedForPayment) {

                binding.tvStatus.text = getString(R.string.order_is_sent)

                binding.bottomBtn.text = getString(R.string.ask_for_payment)
                binding.bottomBtn.visibility = View.VISIBLE
            } else {
                binding.bottomBtn.visibility = View.INVISIBLE
                binding.tvStatus.text = getString(R.string.waiting_for_payment)


            }
        }
    }

    private fun askForPayment() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val res = ordersVM.basketAskForPayment()
            withContext(Dispatchers.Main) {
                binding.swipeRefresh.isRefreshing = false
                handleNetworkResult(
                    result = res,
                    onSuccess = {
                        ordersVM.currentOrder.askedForPayment = true
                        binding.bottomBtn.visibility = View.INVISIBLE
                        binding.tvStatus.text = getString(R.string.waiting_for_payment)
                    },
                    onUnauthorized = { (requireActivity() as MainActivity).logout() },
                    retry = {
                        askForPayment()
                    })
            }
        }
    }

    private fun observeBasket() {
        ordersVM.orderBasket.observe(viewLifecycleOwner) { res ->
            if (res != null && res.isNotEmpty()) {
                binding.swipeRefresh.isRefreshing = false

                (binding.itemRv.adapter as OrderItemEditAdapter).isEditEnabled = false

                if (ordersVM.currentOrder.isLocal) {
                    (binding.itemRv.adapter as OrderItemEditAdapter).isEditEnabled = true
                    binding.clearBtn.visibility = View.VISIBLE
                    binding.tvStatus.text = getString(R.string.order_is_not_sent)
                } else if (!ordersVM.currentOrder.askedForPayment) {
                    binding.clearBtn.visibility = View.INVISIBLE

                    binding.tvStatus.text = getString(R.string.order_is_sent)
                } else {
                    binding.clearBtn.visibility = View.INVISIBLE

                    binding.tvStatus.text = getString(R.string.waiting_for_payment)
                }

                (requireActivity() as MainActivity).setOrderBadge(1, true)
                (binding.itemRv.adapter as OrderItemEditAdapter).submitList(res)
                (binding.itemRv.adapter as OrderItemEditAdapter).notifyDataSetChanged()
            } else if (res != null && res.isEmpty()) {
                (binding.itemRv.adapter as OrderItemEditAdapter).submitList(res)
                initFragment()
                (requireActivity() as MainActivity).setOrderBadge(0, false)
            }
        }
    }


    private fun initRv() {
        val itemAdapter = OrderItemEditAdapter(this)
        binding.itemRv.apply {
            adapter = itemAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                ordersVM.loadBasket().join()
                withContext(Dispatchers.Main) {
                    binding.swipeRefresh.isRefreshing = false
                }

            }

        }
    }

    override fun onCountChange(orderItem: OrderItem, newCount: Int) {
        ordersVM.updateLocalCount(orderItem, newCount)
    }

}