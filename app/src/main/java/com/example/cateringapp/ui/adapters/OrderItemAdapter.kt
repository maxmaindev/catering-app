package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.databinding.ItemOrderitemMenuBinding
import com.example.cateringapp.utils.loadImgUrl

class OrderItemAdapter() :
    ListAdapter<OrderItem, OrderItemAdapter.OrderItemViewHolder>(ORDER_ITEM_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderitemMenuBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemOrderitemMenuBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(orderItem: OrderItem){
            val menuItem = orderItem.menuItem
            binding.tvName.text = menuItem.name
            binding.tvDesc.text = menuItem.description
            binding.tvMetrics.text = menuItem.metrics
            binding.tvCountValue.text = orderItem.count.toString()
            binding.tvTotalValue.text = "${menuItem.price * orderItem.count}₴"
            loadImgUrl(menuItem.imgUrl,binding.img, R.drawable.baseline_food_bank_24)

        }
    }

    companion object {
        private val ORDER_ITEM_DIFF_UTIL =object : DiffUtil.ItemCallback<OrderItem>() {
            override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean =
                oldItem == newItem
        }
    }
}

