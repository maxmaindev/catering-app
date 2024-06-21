package com.example.cateringapp.ui.client.home.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.databinding.FragmentBusinessesBinding
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.ui.adapters.BusinessAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), BusinessAdapter.OnItemClickListener {

    private val businessesViewModel: BusinessesViewModel by viewModels()

    private var _binding: FragmentBusinessesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBusinessesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
    }

    private fun fragmentInit() {
        businessesViewModel.loadBusinesses()
        initRv()
        setupBusinessesListener()
    }

    private fun setupBusinessesListener() {
        businessesViewModel.businessesRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = {data ->
                    (binding.bisRv.adapter as BusinessAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout()},
                retry = {
                    businessesViewModel.loadBusinesses()

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
        val dir = HomeFragmentDirections.actionNavigationHomeToNavigationMenu(business.id)
        findNavController().navigate(dir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}