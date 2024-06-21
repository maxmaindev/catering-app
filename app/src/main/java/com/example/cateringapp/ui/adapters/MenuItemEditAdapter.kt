package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemMenuitemOrderBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.utils.loadImgUrl
import com.example.cateringapp.utils.setGone
import com.example.cateringapp.utils.setInvisible
import com.example.cateringapp.utils.setVisible

class MenuItemEditAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<MenuItemRes, MenuItemEditAdapter.OrderItemViewHolder>(MenuItemAdapter.MENUITEM_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val binding = ItemMenuitemOrderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderItemViewHolder(
        private val binding: ItemMenuitemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root){

        var itemCount: Int = 1
        var editEnabled : Boolean = false
        var price = 0.0f

        fun bind(menuItem: MenuItemRes){
            price = menuItem.price
            binding.tvName.text = menuItem.name
            binding.tvDesc.text = menuItem.description
            binding.tvMetrics.text = menuItem.metrics
            binding.tvCountValue.text = itemCount.toString()
            binding.tvTotalValue.text = "${menuItem.price * itemCount}₴"
            loadImgUrl(menuItem.imgUrl,binding.img, R.drawable.baseline_food_bank_24)

            if (editEnabled) binding.editState.setVisible() else binding.editState.setGone()
            binding.apply {
                textviewMinus.setOnClickListener { editCount(itemCount - 1) }
                textviewPlus.setOnClickListener { editCount(itemCount + 1) }
                btnAdd.setOnClickListener {
                    editState.setVisible()
                    btnAdd.setInvisible()
                    btnSend.setVisible()
                }
                btnSend.setOnClickListener {
                    listener.onItemClick(menuItem, itemCount)
                    editState.setInvisible()
                    btnSend.setInvisible()
                    btnAdd.setVisible()
                }
            }


        }

        private fun editCount(newCount: Int) {
            if (newCount > 0){
                itemCount = newCount
                binding.tvCountValue.text = itemCount.toString()
                binding.tvTotalValue.text = (itemCount * price).toString()+ "грн"
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(menuItem: MenuItemRes, newCount: Int)

    }
}

