package com.example.cateringapp.ui.client.home.menu

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.ui.adapters.MenuItemAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuFragment : Fragment(), MenuItemAdapter.OnItemClickListener {

    private val menuViewModel: MenuViewModel by activityViewModels()

    val args: MenuFragmentArgs by navArgs()

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addMenu()
        fragmentInit()
    }

    private fun fragmentInit() {
        menuViewModel.loadMenu(args.businessId)
        initRv()
        setupMenuListObserver()
    }

    private fun setupMenuListObserver() {
        menuViewModel.menuRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    (binding.menuRv.adapter as MenuItemAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    menuViewModel.loadMenu(args.businessId)
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
        val dir = MenuFragmentDirections.actionNavigationMenuToNavigationMenuItem(menuItem.id)
        findNavController().navigate(dir)
    }

    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.news_menu, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_news -> {
                        val dir = MenuFragmentDirections.actionNavigationMenuToNavigationNewsList(args.businessId)
                        findNavController().navigate(dir)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}