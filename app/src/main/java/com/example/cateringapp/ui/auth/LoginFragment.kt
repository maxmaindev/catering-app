package com.example.cateringapp.ui.auth


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.AuthActivity
import com.example.cateringapp.AuthViewModel
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentLoginBinding
import com.example.cateringapp.remote.res.AuthData
import com.example.cateringapp.remote.res.UserRole
import com.example.cateringapp.remote.utils.NetworkResult
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val authViewModel: AuthViewModel by viewModels()

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSignUp()
        setupLogin()
        setupResultReceiver()
    }

    private fun setupSignUp() {
        binding.textOrSignUp.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_navigation_login_to_navigation_reg)
        }
    }

    private fun setupLogin() {
        binding.btnLogin.setOnClickListener {
            val email = binding.editTextLogin.text.toString()
            val pwd = binding.editTextPassword.text.toString()
            val isValidated: Boolean = validateLoginInput(email, pwd)
            if (isValidated) {
                binding.btnLogin.isEnabled = false
                authViewModel.login(email, pwd)
            }
        }
    }

    private fun validateLoginInput(email: String, pwd: String): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            binding.editTextLogin.error = getString(R.string.invalid_email)
            return false
        }
        if (pwd.isBlank()) {
            binding.editTextLogin.error = getString(R.string.enter_password)
            return false
        }
        return true
    }

    private fun setupResultReceiver() {
        authViewModel.authActionResult.observe(viewLifecycleOwner) { res ->
            if (res !is NetworkResult.Success) {
                binding.btnLogin.isEnabled = true
            }
            when (res) {
                is NetworkResult.Success -> onLoginSuccess(res.data)
                NetworkResult.SockTimeout ->
                    showSnackBar(getString(R.string.server_unavailable))

                NetworkResult.ConnError ->
                    showSnackBar(getString(R.string.internet_connection_error))

                else ->
                    showSnackBar(getString(R.string.network_error))
            }
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun onLoginSuccess(data: AuthData) {
        if (data.user.role == UserRole.Admin.role) {
            val dir = LoginFragmentDirections.actionNavigationLoginToNavigationAdminSetBusiness()
            findNavController().navigate(dir)
        } else {
            (requireActivity() as AuthActivity).openHomeActivity()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}