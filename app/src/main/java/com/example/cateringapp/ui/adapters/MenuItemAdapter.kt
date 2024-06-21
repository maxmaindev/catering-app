package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemMenuBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.utils.AppConsts.imgsEndpoint
import com.example.cateringapp.utils.loadImgUrl
import com.squareup.picasso.Picasso

class MenuItemAdapter(
    private val listener: OnItemClickListener
) :  ListAdapter<MenuItemRes, MenuItemAdapter.BusinessAdapterViewHolder>(MENUITEM_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessAdapterViewHolder {
        val binding = ItemMenuBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BusinessAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusinessAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BusinessAdapterViewHolder(
        private val binding: ItemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(menuItem: MenuItemRes){
            binding.tvName.text = menuItem.name
            binding.tvDesc.text = menuItem.description
            binding.tvMetrics.text = menuItem.metrics
            binding.tvPrice.text = "${menuItem.price}₴"
            loadImgUrl(menuItem.imgUrl,binding.img, R.drawable.baseline_food_bank_24)

            binding.root.setOnClickListener {
                listener.onItemClick(menuItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(menuItem: MenuItemRes)
    }

    companion object {
         val MENUITEM_DIFF_UTIL =object : DiffUtil.ItemCallback<MenuItemRes>() {
            override fun areItemsTheSame(oldItem: MenuItemRes, newItem: MenuItemRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MenuItemRes, newItem: MenuItemRes): Boolean =
                oldItem == newItem
        }
    }
}

