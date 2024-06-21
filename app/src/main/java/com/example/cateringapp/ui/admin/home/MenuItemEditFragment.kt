package com.example.cateringapp.ui.admin.home

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemEditBinding
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.utils.*
import com.example.cateringapp.utils.shortToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class MenuItemEditFragment : Fragment() {
    private val menuViewModel: AdminMenuViewModel by activityViewModels()

    private var _binding: FragmentMenuItemEditBinding? = null
    private val binding get() = _binding!!

    val args: MenuItemEditFragmentArgs by navArgs()

    private var selectedImg: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMenuItemEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
    }

    private fun fragmentInit() {
        insertData()
        pickImageSetup()
        binding.btnUpdate.setOnClickListener { doUpdate() }
        binding.btnDelete.setOnClickListener { doDelete() }
        checkEditResult()
    }

    private fun doDelete() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val menuItem = menuViewModel.getItemById(args.menuItemId) ?: return@launch
            val res = menuViewModel.removeItem(menuItem)
            withContext(Dispatchers.Main) {
                if (res is NetworkResult.Success)
                    findNavController().popBackStack()
                else
                    shortToast(requireContext(), getString(R.string.can_t_delete_the_item))
            }
        }
    }

    private fun checkEditResult() {
        menuViewModel.editItemRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    findNavController().popBackStack()
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    doDelete()
                })
        }
    }

    private fun insertData() {
        val menuItem = menuViewModel.getItemById(args.menuItemId) ?: return
        binding.apply {
            editTextName.setText(menuItem.name)
            editTextDecr.setText(menuItem.description)
            editTextMetrics.setText(menuItem.metrics)
            editTextPrice.setText(menuItem.price.toString())
        }
        loadImgUrl(menuItem.imgUrl, binding.img)
    }

    private fun doUpdate() {
        updateItemValues()
    }

    private fun updateItemValues() {
        binding.apply {
            val newData = MenuItemReq(
                name = editTextName.text.toString(),
                description = editTextDecr.text.toString(),
                metrics = editTextMetrics.text.toString(),
                price = editTextPrice.text.toString().toFloat()
            )
            val img = selectedImg?.let { requireContext().uriToRequestBody(it) }
            menuViewModel.editItem(args.menuItemId, newData, img)
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


    fun Context.uriToRequestBody(uri: Uri): MultipartBody.Part {
        val file = File(cacheDir, "upload_file")
        contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val requestBody = file
            .asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())

        val fileName = "img.${getFileExtension(uri)}"
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }

    fun isFileTypeValid(): Boolean {
        val mimeType = requireContext().contentResolver.getType(selectedImg!!)

        return !(mimeType != "image/png" && mimeType != "image/jpeg")
    }

    fun Context.getFileExtension(uri: Uri): String? {
        val mimeType: String? = contentResolver.getType(uri)
        val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        return fileExtension
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}