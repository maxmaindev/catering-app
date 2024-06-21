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
import com.example.cateringapp.databinding.ItemBusinessBookingBinding
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.BookingStatus
import com.example.cateringapp.remote.res.translateBookingStatus
import com.example.cateringapp.utils.convertUtcToLocal

class BusinessBookingAdapter(
    private val listener: OnItemClickListener,
) : ListAdapter<BookingRes, BusinessBookingAdapter.BusinessBookingsAdapterViewHolder>(
    BOOKING_DIFF_UTIL
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BusinessBookingsAdapterViewHolder {
        val binding = ItemBusinessBookingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return BusinessBookingsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusinessBookingsAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BusinessBookingsAdapterViewHolder(
        private val binding: ItemBusinessBookingBinding,
    ) : RecyclerView.ViewHolder(binding.root), AdapterView.OnItemSelectedListener {

        lateinit var _booking : BookingRes

        fun bind(booking: BookingRes) {
            _booking = booking
            val user = booking.user!!
            binding.apply {
                tvNameSurname.text = user.name +" "+ user.surname
                tvPhone.text = user.phone
                tvBookingDateTime.text = convertUtcToLocal(booking.date)
                tvPeopleCount.text = booking.tableSize.toString()
            }
            setupSpinner(booking.status)

        }
        private fun setupSpinner(status: BookingStatus) {
            val bookingStatusesText = BookingStatus.entries.map { translateBookingStatus(it) }
            val selectedIndex = BookingStatus.entries.indexOfFirst { it == status }

            val spinner: Spinner = binding.spStatus
            // Create an ArrayAdapter using the string array and a default spinner layout.
            val arrayAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                bookingStatusesText
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
            val entry = BookingStatus.entries[position]
            listener.onSpinnerClick(_booking,entry)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            //shortToast(binding.root.context, "NOTHING SELECTED SPINNER")
        }


    }

    interface OnItemClickListener {
        fun onSpinnerClick(booking: BookingRes, status: BookingStatus)
    }

    companion object {
        private val BOOKING_DIFF_UTIL = object : DiffUtil.ItemCallback<BookingRes>() {
            override fun areItemsTheSame(oldItem: BookingRes, newItem: BookingRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BookingRes, newItem: BookingRes): Boolean =
                oldItem == newItem
        }
    }
}

