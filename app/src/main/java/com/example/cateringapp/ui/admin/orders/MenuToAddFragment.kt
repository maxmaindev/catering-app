package com.example.cateringapp.ui.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.databinding.FragmentAdminMenuBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.ui.adapters.MenuItemEditAdapter
import com.example.cateringapp.ui.admin.home.AdminHomeViewModel
import com.example.cateringapp.ui.admin.home.AdminMenuViewModel
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.setGone
import com.example.cateringapp.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MenuToAddFragment : Fragment(), MenuItemEditAdapter.OnItemClickListener {

    private val menuViewModel: AdminMenuViewModel by activityViewModels()
    private val adminHomeVM: AdminHomeViewModel by activityViewModels()
    private val adminOrdersVM: AdminOrdersViewModel by activityViewModels()

    val args: MenuToAddFragmentArgs by navArgs()

    private var _binding: FragmentAdminMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminMenuBinding.inflate(inflater, container, false)
        binding.fab.setGone()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
    }

    private fun fragmentInit() {
        initRv()
        loadSelectedBusinessMenu()
        setupMenuListObserver()
    }


    private fun loadSelectedBusinessMenu() {
        menuViewModel.loadMenu()
    }

    private fun setupMenuListObserver() {
        menuViewModel.menuRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    (binding.menuRv.adapter as MenuItemEditAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    loadSelectedBusinessMenu()
                })
        }
    }

    private fun initRv() {
        val menuAdapter = MenuItemEditAdapter(this)
        binding.menuRv.apply {
            adapter = menuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(menuItem: MenuItemRes, newCount: Int) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val res = adminOrdersVM.addItemToRemote(
                orderId = args.orderId,
                menuItemId = menuItem.id,
                count = newCount
            )
            if (res !is NetworkResult.Success) {
                shortToast(requireContext(), "Error")
            }
        }

    }


}