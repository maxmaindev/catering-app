package com.example.cateringapp.ui.admin.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentAdminMenuBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.ui.adapters.MenuItemAdapter
import com.example.cateringapp.ui.client.home.menu.MenuFragmentDirections
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.setGone
import com.example.cateringapp.utils.setVisible
import com.example.cateringapp.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AdminHomeFragment : Fragment(), MenuItemAdapter.OnItemClickListener {

    private val menuViewModel: AdminMenuViewModel by activityViewModels()
    private val adminHomeVM: AdminHomeViewModel by activityViewModels()

    private var _binding: FragmentAdminMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
        addMenu()
    }

    private fun fragmentInit() {
        initRv()
        setupFab()
        loadSelectedBusiness()
        setupMenuListObserver()
    }

    private fun setupFab() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_home_to_navigation_new_menu_item)
        }
    }

    private fun loadSelectedBusiness(){
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val businessId = adminHomeVM.getSelectedBusiness()
            withContext(Dispatchers.Main) {
                if (businessId == null) {
                    shortToast(
                        requireContext(),
                        getString(R.string.cant_load_business_selected_business)
                    )
                } else {
                    menuViewModel.loadMenu(businessId)
                }
            }
        }
    }

    private fun setupMenuListObserver() {
        menuViewModel.menuRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    if (data.isNullOrEmpty()) binding.tvEmpty.setVisible()
                    else binding.tvEmpty.setGone()
                    (binding.menuRv.adapter as MenuItemAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    loadSelectedBusiness()

                })
        }
    }

    private fun initRv() {
        val menuAdapter = MenuItemAdapter(this)
        binding.menuRv.apply {
            adapter = menuAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(menuItem: MenuItemRes) {
        val dir = AdminHomeFragmentDirections
            .actionNavigationAdminHomeToNavigationEditMenuItem(menuItem.id)
        findNavController().navigate(dir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.news_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_news -> {
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                            val businessId = adminHomeVM.getSelectedBusiness() ?: return@launch
                            withContext(Dispatchers.Main){
                                val dir = AdminHomeFragmentDirections
                                    .actionNavigationAdminHomeToNavigationAdminNewsList(businessId)
                                findNavController().navigate(dir)
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }
}