package com.example.cateringapp.ui.admin.home.news

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemEditBinding
import com.example.cateringapp.databinding.FragmentNewsEditBinding
import com.example.cateringapp.remote.req.MenuItemReq
import com.example.cateringapp.remote.req.NewsEditReq
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
class NewsEditFragment : Fragment() {
    private val newsVM: AdminNewsViewModel by activityViewModels()

    private var _binding: FragmentNewsEditBinding? = null
    private val binding get() = _binding!!

    val args: NewsEditFragmentArgs by navArgs()

    private var selectedImg: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsEditBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnUpdate.text = getString(R.string.update)
        binding.btnDelete.visibility = View.VISIBLE
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
            val news = newsVM.getItemById(args.newsId) ?: return@launch
            val res = newsVM.removeItem(news.id)
            withContext(Dispatchers.Main) {
                if (res is NetworkResult.Success)
                    findNavController().popBackStack()
                else
                    shortToast(requireContext(), getString(R.string.can_t_delete_the_item))
            }
        }
    }

    private fun checkEditResult() {
        newsVM.editNewsRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    findNavController().popBackStack()
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    doUpdate()
                })
        }
    }

    private fun insertData() {
        val news = newsVM.getItemById(args.newsId) ?: return
        binding.apply {
            editTextName.setText(news.title)
            editTextDescText.setText(news.text)
        }
        loadImgUrl(news.imgUrl, binding.img)
    }

    private fun doUpdate() {
        updateItemValues()
    }

    private fun updateItemValues() {
        binding.apply {
            val newData = NewsEditReq(
                title = editTextName.text.toString(),
                text = editTextDescText.text.toString()
            )
            val img = selectedImg?.let { requireContext().uriToRequestBody(it) }
            newsVM.editItem(args.newsId, newData, img)
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