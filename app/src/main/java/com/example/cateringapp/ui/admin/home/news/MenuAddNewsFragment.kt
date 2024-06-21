package com.example.cateringapp.ui.admin.home.news

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemEditBinding
import com.example.cateringapp.databinding.FragmentNewsEditBinding
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.req.NewsEditReq
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.loadImgUri
import com.example.cateringapp.utils.uriToRequestBody
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MenuAddNewsFragment : Fragment() {
    private val newsVM: AdminNewsViewModel by viewModels()

    private var _binding: FragmentNewsEditBinding? = null
    private val binding get() = _binding!!

    private var selectedImg: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsEditBinding.inflate(inflater, container, false)
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
        newsVM.addNewsRes.observe(viewLifecycleOwner) { res ->
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
            val newData = NewsEditReq(
                title = editTextName.text.toString(),
                text = editTextDescText.text.toString(),
            )
            val img = selectedImg?.let { requireContext().uriToRequestBody(it) }
            newsVM.newItem(newData,img)
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