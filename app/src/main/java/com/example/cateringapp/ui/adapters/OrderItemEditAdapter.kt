package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.core.models.OrderItem
import com.example.cateringapp.databinding.ItemOrderitemEditedMenuBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.utils.loadImgUrl
import com.example.cateringapp.utils.setGone
import com.example.cateringapp.utils.setInvisible
import com.example.cateringapp.utils.setVisible

class OrderItemEditAdapter(
    private val listener: OnItemClickListener,
    var isEditEnabled: Boolean = false
) :
    ListAdapter<OrderItem, OrderItemEditAdapter.OrderItemViewHolder>(ORDER_ITEM_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemOrderitemEditedMenuBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemOrderitemEditedMenuBinding
    ) : RecyclerView.ViewHolder(binding.root){

        var itemCount: Int = 1

        fun bind(orderItem: OrderItem){
            val menuItem = orderItem.menuItem
            binding.tvName.text = menuItem.name
            binding.tvDesc.text = menuItem.description
            binding.tvMetrics.text = menuItem.metrics
            binding.tvCountValue.text = orderItem.count.toString()
            binding.tvTotalValue.text = "${menuItem.price * orderItem.count}₴"
            loadImgUrl(menuItem.imgUrl,binding.img, R.drawable.baseline_food_bank_24)

            if (isEditEnabled) binding.editBtns.setVisible() else binding.editBtns.setGone()
            binding.textviewMinus.setOnClickListener { editCount(orderItem ,itemCount - 1) }
            binding.textviewPlus.setOnClickListener { editCount(orderItem ,itemCount+1) }
        }

        private fun editCount(orderItem: OrderItem, newCount: Int) {
            if (newCount > 0){
                itemCount = newCount
                binding.tvCountValue.text = itemCount.toString()
                listener.onCountChange(orderItem, itemCount)
            }
        }
    }

    interface OnItemClickListener {
        fun onCountChange(orderItem: OrderItem, newCount: Int)

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

