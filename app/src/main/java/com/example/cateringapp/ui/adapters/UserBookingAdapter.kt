package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemUserBookingBinding
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.BookingStatus
import com.example.cateringapp.remote.res.translateBookingStatus
import com.example.cateringapp.utils.convertUtcToLocal
import com.example.cateringapp.utils.loadImgUrl

class UserBookingAdapter(
    private val listener: OnItemClickListener,
    private val isFinished: Boolean = false
) :  ListAdapter<BookingRes, UserBookingAdapter.UserBookingsAdapterViewHolder>(BOOKING_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBookingsAdapterViewHolder {
        val binding = ItemUserBookingBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserBookingsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserBookingsAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserBookingsAdapterViewHolder(
        private val binding: ItemUserBookingBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(booking: BookingRes){
            val business = booking.business!!
            if (booking.status.status == BookingStatus.CanceledByUser.status){
                binding.root.isEnabled = false
                binding.imgCancel.visibility = View.GONE
            }
            binding.apply {
                tvName.text = business.name
                tvAddress.text = business.address
                tvBookingDateTime.text = convertUtcToLocal(booking.date)
                tvPeopleCount.text = booking.tableSize.toString()
                tvStatus.text = translateBookingStatus(booking.status)
            }
            loadImgUrl(business.listImgUrl,binding.img, R.drawable.baseline_food_bank_24)
            if (isFinished)
                binding.imgCancel.visibility = View.INVISIBLE

            binding.imgCancel.setOnClickListener {
                listener.onCancelClick(booking)
            }
        }
    }

    interface OnItemClickListener {
        fun onCancelClick(booking: BookingRes)
    }

    companion object {
        private val BOOKING_DIFF_UTIL =object : DiffUtil.ItemCallback<BookingRes>() {
            override fun areItemsTheSame(oldItem: BookingRes, newItem: BookingRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: BookingRes, newItem: BookingRes): Boolean =
                oldItem == newItem
        }
    }
}

