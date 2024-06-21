package com.example.cateringapp.ui.admin.home.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentRvBinding
import com.example.cateringapp.databinding.FragmentRvBottomFabBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.ui.adapters.MenuItemAdapter
import com.example.cateringapp.ui.adapters.NewsAdapter
import com.example.cateringapp.ui.client.home.news.NewsViewModel
import com.example.cateringapp.utils.handleNetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminNewsFragment : Fragment(), NewsAdapter.OnItemClickListener {

    private val newsVM: AdminNewsViewModel by activityViewModels()

    val args: AdminNewsFragmentArgs by navArgs()

    private var _binding: FragmentRvBottomFabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRvBottomFabBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentInit()
        binding.fab.visibility = View.VISIBLE
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_news_list_to_navigation_admin_news_add)
        }
    }

    private fun fragmentInit() {
        newsVM.loadNews(args.businessId)
        initRv()
        setupNewsListObserver()
    }

    private fun setupNewsListObserver() {
        newsVM.newsRes.observe(viewLifecycleOwner) { res ->
            handleNetworkResult(
                result = res,
                onSuccess = { data ->
                    (binding.itemRv.adapter as NewsAdapter).submitList(data)
                },
                onUnauthorized = { (requireActivity() as MainActivity).logout() },
                retry = {
                    newsVM.loadNews(args.businessId)
                })
        }
    }

    private fun initRv() {
        val newsAdapter = NewsAdapter(this)
        binding.itemRv.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onItemClick(news: NewsRes) {
        val dir = AdminNewsFragmentDirections.actionNavigationAdminNewsListToNavigationAdminNewsEdit(news.id)
        findNavController().navigate(dir)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}