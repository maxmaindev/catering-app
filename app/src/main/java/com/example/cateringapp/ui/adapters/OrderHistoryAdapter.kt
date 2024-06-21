package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemOrderBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.utils.convertUtcToLocal
import com.example.cateringapp.utils.loadImgUrl

class OrderHistoryAdapter(
    private val listener: OnItemClickListener
) :  ListAdapter<OrderRes, OrderHistoryAdapter.OrderAdapterViewHolder>(ORDER_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapterViewHolder {
        val binding = ItemOrderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderAdapterViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(order: OrderRes){
            val business = order.business!!
            binding.tvName.text = business.name
            binding.tvOrderDateTime.text = convertUtcToLocal(order.date)
            binding.tvTotalValue.text = order.items!!.sumOf { (it.count * it.menuItem.price).toInt() }.toString() + " грн"
            binding.tvInfo.text = order.info

            loadImgUrl(business.listImgUrl,binding.img, R.drawable.baseline_food_bank_24)

            binding.root.setOnClickListener {
                listener.onItemClick(order)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(order: OrderRes)
    }

    companion object {
        private val ORDER_DIFF_UTIL =object : DiffUtil.ItemCallback<OrderRes>() {
            override fun areItemsTheSame(oldItem: OrderRes, newItem: OrderRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: OrderRes, newItem: OrderRes): Boolean =
                oldItem == newItem
        }
    }
}

