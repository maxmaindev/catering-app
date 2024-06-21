package com.example.cateringapp.ui.client.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentProfileEditBinding
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.ui.auth.LoginFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileEditFragment : Fragment() {

    private val profileViewModel: ProfileViewModel by activityViewModels()
    //private val sharedVM: SharedViewModel by activityViewModels ()

    private var _binding: FragmentProfileEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentInfo()
    }

    private fun initFragmentInfo() {
        val res = profileViewModel.userInfo.value
        if (res == null || (res !is NetworkResult.Success)) {
            findNavController().popBackStack()
            return
        }
        val user = res.data
        binding.editTextName.setText(user.name)
        binding.editTextSurname.setText(user.surname)
        binding.editTextEmail.setText(user.email)
        binding.editTextPhone.setText(user.phone)
        binding.btnUpdate.setOnClickListener { updateUserInfo() }

    }

    private fun updateUserInfo() {
        val email = binding.editTextEmail.text.toString()
        val phone = binding.editTextPhone.text.toString()
        val name = binding.editTextName.text.toString()
        val surname = binding.editTextSurname.text.toString()
        val isValidated: Boolean = validateRegInput(email, phone, name, surname)
        if (isValidated)
            binding.btnUpdate.isEnabled = false
        sendUserInfo(name = name, surname = surname, email = email, phone = phone)

    }

    private fun validateRegInput(
        email: String,
        phone: String,
        name: String,
        surname: String,
    ): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            binding.editTextEmail.error = getString(R.string.invalid_email)
            return false
        }
        if (phone.isBlank()) {
            binding.editTextPhone.error = getString(R.string.enter_phone_number)
            return false
        }
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.enter_name)
            return false
        }
        if (surname.isBlank()) {
            binding.editTextSurname.error = getString(R.string.enter_surname)
            return false
        }
        return true
    }

    private fun sendUserInfo(
        name: String,
        surname: String,
        email: String,
        phone: String,
    ) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val res = profileViewModel.updateUserInfo(name, surname, email, phone)
            withContext(Dispatchers.Main) {
                when (res) {
                    is NetworkResult.Success -> onResultSuccess()
                    NetworkResult.SockTimeout ->
                        showSnackBar(getString(R.string.server_unavailable))

                    NetworkResult.ConnError ->
                        showSnackBar(getString(R.string.internet_connection_error))

                    else ->
                        showSnackBar(getString(R.string.network_error))
                }
            }
        }

    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onResultSuccess() {
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}