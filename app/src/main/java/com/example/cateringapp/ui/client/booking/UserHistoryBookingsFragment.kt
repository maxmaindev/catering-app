package com.example.cateringapp.ui.client.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentUserBookingsBinding
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.ui.adapters.UserBookingAdapter
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserHistoryBookingsFragment() : Fragment(), UserBookingAdapter.OnItemClickListener {
    private val bookingVM: BookingViewModel by activityViewModels()

    private var _binding: FragmentUserBookingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserBookingsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.fab.visibility = View.INVISIBLE
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        bookingVM.loadHistoryUserBookings()
        initRv()
        setupBookingsListener()
        onCreateNewBooking()
    }

    private fun onCreateNewBooking() {
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_booking_to_navigation_business_for_booking)
        }
    }

    private fun initRv() {
        val bisAdapter = UserBookingAdapter(this, true)
        binding.bookingRv.apply {
            adapter = bisAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupBookingsListener() {
        bookingVM.userHistoryBooking.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    (binding.bookingRv.adapter as UserBookingAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    bookingVM.loadHistoryUserBookings()

                })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancelClick(booking: BookingRes) {}

}

