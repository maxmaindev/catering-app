package com.example.cateringapp.ui.client.booking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.SharedViewModel
import com.example.cateringapp.databinding.FragmentNewBookingBinding
import com.example.cateringapp.remote.req.BookingReq
import com.example.cateringapp.remote.res.BookingRes
import com.example.cateringapp.remote.utils.NetworkResult
import com.example.cateringapp.ui.client.home.business.BusinessesViewModel
import com.example.cateringapp.utils.handleNetworkResult
import com.example.cateringapp.utils.loadImgUrl
import com.example.cateringapp.utils.makeTwoDigit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

@AndroidEntryPoint
class NewBookingFragment : Fragment() {

    private val businessesViewModel: BusinessesViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val bookingVM: BookingViewModel by activityViewModels()

    private var _binding: FragmentNewBookingBinding? = null
    private val binding get() = _binding!!

    val args: NewBookingFragmentArgs by navArgs()

    private var selectedDateTime: Calendar = Calendar.getInstance()
    private var peopleCount: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewBookingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        initBusinessInfo()
        initDateTimeInfo()
        binding.customDateView.customDateTextview.setOnClickListener { pickDate() }
        binding.customTimeView.customDateTextview.setOnClickListener { pickTime() }
        binding.btnBook.setOnClickListener { bookTable() }
        setupPeopleCount()
    }

    private fun initDateTimeInfo() {
        val currentDate = Calendar.getInstance()
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        val month = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)
        val hourOfDay = currentDate.get(Calendar.HOUR_OF_DAY)
        val minute = currentDate.get(Calendar.MINUTE)
        setNewDateText(year, month + 1, dayOfMonth)
        setNewTimeText(hourOfDay, minute)
    }

    private fun initBusinessInfo() {
        val business = businessesViewModel.getBusinessById(args.businessId) ?: return
        binding.apply {
            tvName.text = business.name
            tvAddress.text = business.address
            tvWorkingHours.text = business.workingHours
            tvPhone.text = business.phone
        }
        loadImg(business.listImgUrl)
    }

    private fun loadImg(imgUrl: String) {
        loadImgUrl(imgUrl,binding.img, R.drawable.baseline_food_bank_24)

    }//TODO: std img

    private fun setupPeopleCount() {
        binding.btnPlus.setOnClickListener {
            peopleCount++
            binding.tvCount.text = peopleCount.toString()
        }
        binding.btnMinus.setOnClickListener {
            if (peopleCount > 1) {
                peopleCount--
                binding.tvCount.text = peopleCount.toString()
            }
        }
    }

    private fun pickDate() {
        val currentDate = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDateTime.set(Calendar.YEAR, year)
                selectedDateTime.set(Calendar.MONTH, month)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                setNewDateText(year, month + 1, dayOfMonth)
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setNewDateText(year: Int, month: Int, dayOfMonth: Int) {
        val text = "${dayOfMonth.makeTwoDigit()}.${month.makeTwoDigit()}.$year"
        binding.customDateView.tvDate.text = text
    }

    private fun pickTime() {
        val currentTime = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedDateTime.set(Calendar.MINUTE, minute)
            selectedDateTime.set(Calendar.SECOND, 0)
            selectedDateTime.set(Calendar.MILLISECOND, 0)
            setNewTimeText(hourOfDay, minute)

        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true).show()
    }

    private fun setNewTimeText(hourOfDay: Int, minute: Int) {
        val text = "${hourOfDay.makeTwoDigit()}:${minute.makeTwoDigit()}"
        binding.customTimeView.tvDate.text = text
    }

    private fun bookTable() {
        // Convert to UTC time
        val utcDateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        utcDateTime.timeInMillis = selectedDateTime.timeInMillis
        val epochMillis = utcDateTime.timeInMillis
        Log.d("Booking", "Selected time in UTC (epoch millis): $epochMillis")


        val businessId = args.businessId
        val userId = sharedViewModel.currentUser.value!!.id
        val tableSize = binding.tvCount.text.toString().toInt()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val res = bookingVM.createUserBookings(
                businessId, BookingReq(
                    businessId = businessId,
                    userId = userId,
                    date = epochMillis,
                    tableSize = tableSize
                )
            )
            withContext(Dispatchers.Main) {
                checkBookingCreation(res)
            }
        }
    }

    private fun checkBookingCreation(res: NetworkResult<BookingRes>) {
        handleNetworkResult(
            result = res,
            onSuccess = { data ->
                findNavController().navigate(R.id.action_navigation_new_booking_to_navigation_booking)
            },
            onUnauthorized = { (requireActivity() as MainActivity).logout() },
            retry = {
                bookTable()
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}