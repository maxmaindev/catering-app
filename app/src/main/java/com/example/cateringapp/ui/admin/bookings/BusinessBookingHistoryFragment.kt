package com.example.cateringapp.ui.admin.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentBusinessBookingBinding
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.BookingStatus
import com.example.cateringapp.ui.adapters.BusinessBookingAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint
import java.time.ZoneOffset

@AndroidEntryPoint
class BusinessBookingHistoryFragment : Fragment(), BusinessBookingAdapter.OnItemClickListener {

    private val bookingVM: AdminBookingViewModel by activityViewModels()

    private var _binding: FragmentBusinessBookingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inf: LayoutInflater, cont: ViewGroup?, savedIS: Bundle?): View {
        _binding = FragmentBusinessBookingBinding.inflate(inf, cont, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        initRv()
        bookingVM.loadFinishedBusinessBookings()
        observeBookings()
        observeUpdated()
        setupRefresh()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            bookingVM.loadFinishedBusinessBookings()
        }
    }

    private fun observeUpdated() {
        bookingVM.updateBooking.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    binding.swipeRefresh.isRefreshing = false

                    bookingVM.loadFinishedBusinessBookings()
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    bookingVM.loadFinishedBusinessBookings()

                })
        }
    }

    private fun observeBookings() {
        bookingVM.historyBusinessBooking.observe(viewLifecycleOwner) { res ->
            binding.swipeRefresh.isRefreshing = false
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    binding.swipeRefresh.isRefreshing = false
                    val items = data.sortedByDescending { it.date.toEpochSecond(ZoneOffset.UTC)}

                    (binding.bookingRv.adapter as BusinessBookingAdapter).submitList(items)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    bookingVM.loadFinishedBusinessBookings()

                })
        }
    }

    private fun initRv() {
        val bisAdapter = BusinessBookingAdapter(this)
        binding.bookingRv.apply {
            adapter = bisAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onSpinnerClick(booking: BookingRes, status: BookingStatus) {
        bookingVM.updateBookingStatus(booking, status)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}