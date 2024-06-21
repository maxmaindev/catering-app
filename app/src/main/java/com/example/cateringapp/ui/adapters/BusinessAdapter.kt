package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemCateringBisBinding
import com.example.cateringapp.remote.res.BusinessRes
import com.example.cateringapp.utils.AppConsts.imgsEndpoint
import com.example.cateringapp.utils.loadImgUrl
import com.squareup.picasso.Picasso

class BusinessAdapter(
    private val listener: OnItemClickListener
) :  ListAdapter<BusinessRes, BusinessAdapter.BusinessAdapterViewHolder>(BUSINESS_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessAdapterViewHolder {
        val binding = ItemCateringBisBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BusinessAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusinessAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BusinessAdapterViewHolder(
        private val binding: ItemCateringBisBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(business: BusinessRes){
            binding.tvName.text = business.name
            binding.tvAddress.text = business.address
            binding.tvWorkingHours.text = business.workingHours
            loadImgUrl(business.listImgUrl,binding.img, R.drawable.baseline_food_bank_24)

            binding.root.setOnClickListener {
                listener.onItemClick(business)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(business: BusinessRes)
    }

    companion object {
        private val BUSINESS_DIFF_UTIL =object : DiffUtil.ItemCallback<BusinessRes>() {
            override fun areItemsTheSame(oldItem: BusinessRes, newItem: BusinessRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BusinessRes, newItem: BusinessRes): Boolean =
                oldItem == newItem
        }
    }
}

