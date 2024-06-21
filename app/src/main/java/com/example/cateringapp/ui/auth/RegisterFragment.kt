package com.example.cateringapp.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.AuthActivity
import com.example.cateringapp.AuthViewModel
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentRegBinding
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.utils.shortToast
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedStateHandle: SavedStateHandle


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRegBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSignIn()
        setupRegister()
        setupResultReceiver()

    }

    private fun setupSignIn() {
        binding.textOrSignIn.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_navigation_reg_to_navigation_login)
        }
    }

    private fun setupRegister() {
        binding.btnReg.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val phone = binding.editTextPhone.text.toString()
            val name = binding.editTextName.text.toString()
            val surname = binding.editTextSurname.text.toString()
            val pwd = binding.editTextPassword.text.toString()
            val isValidated: Boolean = validateRegInput(email,pwd,phone,name,surname)
            if (isValidated)
                binding.btnReg.isEnabled = false
                authViewModel.register(
                    name = name, surname = surname, email = email, pwd = pwd, phone = phone
                )
        }
    }

    private fun validateRegInput(
        email: String,
        pwd: String,
        phone: String,
        name: String,
        surname: String
    ): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            binding.editTextEmail.error = getString(R.string.invalid_email)
            return false
        }
        if (pwd.isBlank()) {
            binding.editTextPassword.error = getString(R.string.enter_password)
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

    private fun setupResultReceiver() {
        authViewModel.authActionResult.observe(viewLifecycleOwner){ res ->
            if (res !is NetworkResult.Success) {
                binding.btnReg.isEnabled = true
            }
            when (res) {
                is NetworkResult.Success -> onRegisterSuccess()
                NetworkResult.SockTimeout ->
                    showSnackBar(getString(R.string.server_unavailable))
                NetworkResult.ConnError ->
                    showSnackBar(getString(R.string.internet_connection_error))
                else ->
                    showSnackBar(getString(R.string.network_error))
            }
        }
    }

    private fun showSnackBar(text: String){
        Snackbar.make(
            binding.root,
            text, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onRegisterSuccess(){
        (requireActivity() as AuthActivity).openHomeActivity()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}