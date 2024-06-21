package com.example.cateringapp.ui.admin.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentOrdersBinding
import com.example.cateringapp.ui.client.orders.OrdersViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminOrdersFragment : Fragment() {

    private val ordersViewModel: OrdersViewModel by activityViewModels()

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentOrdersAdapter: FragmentAdminOrdersAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
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

        fragmentOrdersAdapter = FragmentAdminOrdersAdapter(this)
        viewPager.adapter = fragmentOrdersAdapter

        tabLayout.visibility = View.VISIBLE
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTextFromPosition(position)
        }.attach()
    }

    private fun getTabTextFromPosition(position: Int): CharSequence? {
        if (position == 0)
            return getString(R.string.active_orders)
        else
            return  getString(R.string.order_history)
    }


    class FragmentAdminOrdersAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            if (position == 0)
                return ActiveAdminOrdersFragment()
            else
                return AdminOrderHistoryFragment()

        }
    }
}