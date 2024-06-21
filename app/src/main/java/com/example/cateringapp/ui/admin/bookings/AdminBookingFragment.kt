package com.example.cateringapp.ui.admin.bookings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentAdminBookingBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminBookingFragment : Fragment() {


    private lateinit var fragmentBookingAdapter: FragmentAdminBookingAdapter
    private lateinit var viewPager: ViewPager2

    private var _binding: FragmentAdminBookingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminBookingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewPager()
    }

    private fun createViewPager() {
        viewPager = binding.pager
        val tabLayout = binding.tabLayout

        fragmentBookingAdapter = FragmentAdminBookingAdapter(this)
        viewPager.adapter = fragmentBookingAdapter

        tabLayout.visibility = View.VISIBLE
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTextFromPosition(position)
        }.attach()

    }

    private fun getTabTextFromPosition(position: Int): String {
        if (position == 0) {
            return getString(R.string.active_bookings)
        } else if (position == 1) {
            return getString(R.string.history)
        } else throw UnknownError("Error in the tab indexing")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class FragmentAdminBookingAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            val fragment = BusinessBookingFragment()
            return fragment
        } else {
            val fragment = BusinessBookingHistoryFragment()
            return fragment
        }
        // Return a NEW fragment instance in createFragment(int).

//        fragment.arguments = Bundle().apply {
//            // The object is just an integer.
//            putInt(ARG_OBJECT, position + 1)
//        }
    }
}