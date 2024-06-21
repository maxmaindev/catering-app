package com.example.cateringapp.ui.admin.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentAdminProfileBinding
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.ui.client.home.business.BusinessesViewModel
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.loadImgUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminProfileFragment : Fragment() {

    private val businessVM: BusinessesViewModel by activityViewModels ()

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLogout()
        loadSelectedSelectedBusiness()
        initFragmentInfo()
        initEditBtn()
    }

    private fun initEditBtn() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_profile_to_navigation_edit_business)
        }
    }

    private fun loadSelectedSelectedBusiness() {
        businessVM.loadSelectedBusiness()
    }

    private fun initFragmentInfo() {
        businessVM.selectedBusinessRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    initializeData(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    businessVM.loadSelectedBusiness()
                })
        }

    }

    private fun initializeData(data: BusinessRes) {
        binding.tvName.text = data.name
        binding.btnAddress.text = data.address
        binding.btnPhone.text = data.phone
        binding.btnWorkingHours.text = data.workingHours
        loadImgUrl(data.listImgUrl,binding.imgProfile, R.drawable.baseline_food_bank_24)

    }


    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            (requireActivity() as MainActivity).logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}