package com.example.cateringapp.utils

import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.example.cateringapp.R
import com.squareup.picasso.Picasso

fun loadImgUri(
    imgUri: Uri,
    imageView: ImageView,
    @DrawableRes  placeholderResId: Int = R.drawable.placeholder_rounded_corners
) {
    val picasso = Picasso.get()
    picasso.setIndicatorsEnabled(true)
    picasso.isLoggingEnabled = true
    picasso
        .load(imgUri)
        .placeholder(placeholderResId)
        .into(imageView)
}

fun loadImgUrl(
    imgUrl: String,
    imageView: ImageView,
    @DrawableRes  placeholderResId: Int = R.drawable.placeholder_rounded_corners
){
    val picasso = Picasso.get()
    picasso.setIndicatorsEnabled(true)
    picasso.isLoggingEnabled = true
    picasso
        .load(AppConsts.imgsEndpoint +imgUrl)
        .placeholder(placeholderResId)
        .into(imageView)
}