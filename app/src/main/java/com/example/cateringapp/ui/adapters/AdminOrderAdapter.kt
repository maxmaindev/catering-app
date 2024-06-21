package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.databinding.ItemAdminOrderBinding
import com.example.cateringapp.remote.res.OrderRes
import com.example.cateringapp.remote.res.OrderStatus
import com.example.cateringapp.remote.res.translateOrderStatus
import com.example.cateringapp.utils.convertUtcToLocal

class AdminOrderAdapter(
    private val listener: OnItemClickListener
) :  ListAdapter<OrderRes, AdminOrderAdapter.OrderAdapterViewHolder>(ORDER_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapterViewHolder {
        val binding = ItemAdminOrderBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderAdapterViewHolder(
        private val binding: ItemAdminOrderBinding
    ) : RecyclerView.ViewHolder(binding.root), AdapterView.OnItemSelectedListener{

        lateinit var _order : OrderRes
        fun bind(order: OrderRes){
            _order = order
            val user = order.user ?: return
            binding.tvNameSurname.text = user.name + " " + user.surname
            binding.tvOrderDateTime.text = convertUtcToLocal(order.date)
            binding.tvInfo.text = order.info
            binding.tvTotalValue.text = order.items!!.sumOf { (it.count * it.menuItem.price).toInt() }.toString() + " грн"
            setupSpinner(order.status)
            binding.root.setOnClickListener {
                listener.onItemClick(order)
            }

        }

        private fun setupSpinner(status: OrderStatus) {

            val orderStatuses = OrderStatus.entries.map { translateOrderStatus(it) }
            val selectedIndex = OrderStatus.entries.indexOfFirst { it == status }

            val spinner: Spinner = binding.spStatus
            // Create an ArrayAdapter using the string array and a default spinner layout.
            val arrayAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                orderStatuses
            )
            // Specify the layout to use when the list of choices appears.
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = arrayAdapter

            spinner.setSelection(selectedIndex, false)
            spinner.onItemSelectedListener = this

        }

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedStatus = parent.getItemAtPosition(position) as String
            val entry = OrderStatus.entries[position]
            listener.onSpinnerClick(_order,entry)

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //shortToast(binding.root.context, "NOTHING SELECTED SPINNER")
        }
    }

    interface OnItemClickListener {
        fun onSpinnerClick(order: OrderRes, status: OrderStatus)
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

