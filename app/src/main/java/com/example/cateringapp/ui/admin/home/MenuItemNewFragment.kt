package com.example.cateringapp.ui.admin.home

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
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemEditBinding
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.loadImgUri
import com.example.cateringapp.utils.uriToRequestBody
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuItemNewFragment : Fragment() {
    private val menuViewModel: AdminMenuViewModel by activityViewModels()

    private var _binding: FragmentMenuItemEditBinding? = null
    private val binding get() = _binding!!

    private var selectedImg: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuItemEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnUpdate.text = getString(R.string.create)
        binding.btnDelete.visibility = View.INVISIBLE
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
    }

    private fun fragmentInit() {
        pickImageSetup()
        binding.btnUpdate.setOnClickListener { createNew() }
        setupNewItemListener()
    }
    private fun setupNewItemListener() {
        menuViewModel.newItemRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = {data ->
                    findNavController().popBackStack()
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout()},
                retry = {
                    createNew()
                })
        }
    }


    private fun createNew() {
        binding.apply {
            val newData = MenuItemReq(
                name = editTextName.text.toString(),
                description = editTextDecr.text.toString(),
                metrics = editTextMetrics.text.toString(),
                price = editTextPrice.text.toString().toFloat()
            )
            val img = selectedImg?.let { requireContext().uriToRequestBody(it) }
            menuViewModel.newItem(newData,img)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}