package com.example.cateringapp.utils

import android.content.Context
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cateringapp.AuthActivity
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.remote.utils.NetworkResult
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun shortToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
}

fun longToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
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

fun Context.isFileTypeValid(imgUri: Uri): Boolean {
    val mimeType = contentResolver.getType(imgUri)

    return !(mimeType != "image/png" && mimeType != "image/jpeg")
}

fun Context.getFileExtension(uri: Uri): String? {
    val mimeType: String? = contentResolver.getType(uri)
    val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    return fileExtension
}

fun View.setInvisible(){
    this.visibility = View.INVISIBLE
}

fun View.setVisible(){
    this.visibility = View.VISIBLE
}

fun View.setGone(){
    this.visibility = View.GONE
}

/**
 * Handles NetworkResult network error with SnackBar. If result is "null" the result is ignored.
 * The SnackBar position is set to activity's bottomNav
 */
fun <T> Fragment.handleNetworkResult(
    result: NetworkResult<T>,
    onSuccess: (data: T) -> Unit,
    onUnauthorized: () -> Unit,
    retry: () -> Unit,
    onFailure: ((result: NetworkResult<T>) -> Unit) = {  },
) {
    fun showSnackBarINF(view: View, text: String, retry: () -> Unit) {
        Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
            .setAction(view.context.getString(R.string.update)){
                retry()
            }
            .setAnchorView(view)
            .show()
    }

    val view = if (requireActivity() is MainActivity)
        (requireActivity() as MainActivity).getBottomNav()
    else (requireActivity() as AuthActivity).getAnchor()
    if (result !is NetworkResult.Success){
        onFailure(result)
    }
    when (result) {
        is NetworkResult.Success -> onSuccess(result.data)
        is NetworkResult.Unauthorized -> onUnauthorized()
        NetworkResult.SockTimeout ->
            showSnackBarINF(view, getString(R.string.server_unavailable), retry)
        NetworkResult.ConnError ->
            showSnackBarINF(view, getString(R.string.internet_connection_error),retry)
        else ->
            showSnackBarINF(view, getString(R.string.network_error), retry)
    }
}



