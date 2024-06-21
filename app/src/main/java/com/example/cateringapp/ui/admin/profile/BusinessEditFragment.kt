package com.example.cateringapp.ui.admin.profile

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentBusinessEditBinding
import com.example.cateringapp.remote.req.BusinessEditReq
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.ui.client.home.business.BusinessesViewModel
import com.example.cateringapp.utils.loadImgUri
import com.example.cateringapp.utils.loadImgUrl
import com.example.cateringapp.utils.uriToRequestBody
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class BusinessEditFragment : Fragment() {

    private val businessVM: BusinessesViewModel by activityViewModels()
    private val businessProfileVM: AdminProfileViewModel by activityViewModels()
    //private val sharedVM: SharedViewModel by activityViewModels ()

    private var _binding: FragmentBusinessEditBinding? = null
    private val binding get() = _binding!!

    private var selectedImg: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBusinessEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragmentInfo()
        pickImageSetup() 
    }


    private fun initFragmentInfo() {
        val res = businessVM.selectedBusinessRes.value
        if (res == null || (res !is NetworkResult.Success)) {
            findNavController().popBackStack()
            return
        }
        val business = res.data
        binding.editTextName.setText(business.name)
        binding.editTextAddress.setText(business.address)
        binding.editTextPhone.setText(business.phone)
        binding.editTextWorkingHours.setText(business.workingHours)
        //binding.editTextWebsite.setText(user.website)
        binding.btnUpdate.setOnClickListener { updateBusinessInfo() }
        loadImgUrl(business.listImgUrl, binding.img)

    }

    private fun updateBusinessInfo() {
        val name = binding.editTextName.text.toString()
        val phone = binding.editTextPhone.text.toString()
        val workingHours = binding.editTextWorkingHours.text.toString()
        val address = binding.editTextAddress.text.toString()
        //val website = binding.editTextWebsite.text.toString()
        val isValidated: Boolean = validateRegInput(name, workingHours, phone, address)
        if (isValidated)
            binding.btnUpdate.isEnabled = false
        sendBusinessInfo(name = name, workingHours = workingHours, phone = phone, address = address)
    }

    private fun validateRegInput(
        name: String,
        workingHours: String,
        phone: String,
        address: String,
    ): Boolean {
        if (name.isBlank()) {
            binding.editTextName.error = getString(R.string.enter_name)
            return false
        }
        if (workingHours.isBlank()) {
            binding.editTextWorkingHours.error = getString(R.string.enter_working_time)
            return false
        }
        if (phone.isBlank()) {
            binding.editTextPhone.error = getString(R.string.enter_phone_number)
            return false
        }

        if (address.isBlank()) {
            binding.editTextAddress.error = getString(R.string.enter_address)
            return false
        }
        return true
    }

    private fun sendBusinessInfo(
        name: String,
        workingHours: String,
        phone: String,
        address: String,
    ) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val img = selectedImg?.let { requireContext().uriToRequestBody(it) }
            val res = businessProfileVM.editBusiness(
                newData = BusinessEditReq(name, address, workingHours, phone),
                img
            )
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

    private fun pickImageSetup() {
        // Registers a photo picker activity launcher in single-select mode.
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    selectedImg = uri
                    loadImgUri(uri, binding.img)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
        binding.btnPick.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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