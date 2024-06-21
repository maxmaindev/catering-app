package com.example.cateringapp.ui.client.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentProfileBinding
import com.example.cateringapp.remote.res.UserInfoRes
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by activityViewModels()
    //private val sharedVM: SharedViewModel by activityViewModels ()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getMyUserInfo()
        setupLogout()
        setupUserEdit()
        observeUserInfo()
    }

    private fun setupUserEdit() {
        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_profile_edit)
        }
    }

    private fun observeUserInfo(){
        profileViewModel.userInfo.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    initFragmentInfo(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    profileViewModel.getMyUserInfo()

                })
        }
    }

    private fun initFragmentInfo(user: UserInfoRes) {
        binding.tvName.text = user.name
        binding.tvSurname.text = user.surname
        binding.btnEmail.text = user.email
        binding.btnPhone.text = user.phone

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