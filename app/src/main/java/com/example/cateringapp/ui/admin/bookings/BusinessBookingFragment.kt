package com.example.cateringapp.ui.admin.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentBusinessBookingBinding
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.res.BookingStatus
import com.example.cateringapp.ui.adapters.BusinessBookingAdapter
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.setInvisible
import com.example.cateringapp.utils.setVisible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BusinessBookingFragment : Fragment(), BusinessBookingAdapter.OnItemClickListener {

    private val bookingVM: AdminBookingViewModel by activityViewModels()

    private var _binding: FragmentRvBinding? = null
    private val binding get() = _binding!!

    private var autoUpdateEnabled: Boolean = true


    override fun onCreateView(inf: LayoutInflater, cont: ViewGroup?, savedIS: Bundle?): View {
        _binding = FragmentRvBinding.inflate(inf, cont, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        initRv()
        observeBookings()
        observeUpdated()
        startUpdating()
        setupRefresh()
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            bookingVM.loadActiveBusinessBookings()
        }
    }

    private fun startUpdating() {
        bookingVM.loadActiveBusinessBookings()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            while (autoUpdateEnabled) {
                delay(10000) // Update every 10 second
                bookingVM.loadActiveBusinessBookings()
            }
        }
    }

    private fun observeUpdated() {
        bookingVM.updateBooking.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    bookingVM.loadActiveBusinessBookings()
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                onFailure = {
                    autoUpdateEnabled = false
                },
                retry = {
                    bookingVM.loadActiveBusinessBookings()
                })
        }
    }

    private fun observeBookings() {
        bookingVM.activeBusinessBooking.observe(viewLifecycleOwner) { res ->
            binding.swipeRefresh.isRefreshing = false

            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    if (data.isEmpty())
                        binding.tvEmpty.setVisible()
                    else binding.tvEmpty.setInvisible()
                    autoUpdateEnabled = true
                    (binding.itemRv.adapter as BusinessBookingAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                onFailure = {
                    autoUpdateEnabled = false
                },
                retry = {
                    bookingVM.loadActiveBusinessBookings()
                })
        }
    }

    private fun initRv() {
        val bisAdapter = BusinessBookingAdapter(this)
        binding.itemRv.apply {
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