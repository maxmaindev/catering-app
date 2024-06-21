package com.example.cateringapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.AuthActivity
import com.example.cateringapp.AuthViewModel
import com.example.cateringapp.databinding.FragmentBusinessListBinding
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.ui.adapters.BusinessAdapter
import com.example.cateringapp.ui.client.home.business.BusinessesViewModel
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class AdminBusinessListFragment : Fragment(), BusinessAdapter.OnItemClickListener {

    private val authViewModel: AuthViewModel by viewModels()
    private val businessesViewModel: BusinessesViewModel by viewModels()


    private var _binding: FragmentBusinessListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBusinessListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
    }

    private fun fragmentInit() {
        businessesViewModel.loadUserBusinesses()
        initRv()
        initFab()
        observeBusinesses()
    }

    private fun initFab() {
        binding.fab.setOnClickListener {
            val dir = AdminBusinessListFragmentDirections
                .actionNavigationAdminSetBusinessToNavigationAdminNewBusiness()
            findNavController().navigate(dir)
        }
    }

    private fun observeBusinesses() {
        businessesViewModel.businessesRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    (binding.bisRv.adapter as BusinessAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as AuthActivity).logout() },
                retry = {
                    businessesViewModel.loadUserBusinesses()

                })
        }
    }

    private fun initRv() {
        val bisAdapter = BusinessAdapter(this)
        binding.bisRv.apply {
            adapter = bisAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(business: BusinessRes) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            authViewModel.saveSelectedBusiness(business)
            withContext(Dispatchers.Main) {
                (requireActivity() as AuthActivity).openHomeActivity()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}