package com.example.cateringapp.ui.client.home.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cateringapp.MainActivity
import com.example.cateringapp.R
import com.example.cateringapp.databinding.FragmentMenuItemBinding
import com.example.cateringapp.databinding.FragmentNewsItemBinding
import com.example.cateringapp.remote.res.MenuItemRes
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.ui.client.orders.OrdersViewModel
import com.example.cateringapp.utils.loadImgUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsItemFragment : Fragment() {

    private val newsVM: NewsViewModel by activityViewModels()


    private var _binding: FragmentNewsItemBinding? = null
    private val binding get() = _binding!!

    val args: NewsItemFragmentArgs by navArgs()

    private lateinit var newsItem: NewsRes

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNewsItemBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsItem = newsVM.getItemById(args.newsId)!!
        fragmentInit()
    }

    private fun fragmentInit() {
        loadImg(newsItem.imgUrl)
        binding.apply {
            tvTitle.text = newsItem.title
            tvText.text = newsItem.text
        }
    }


    private fun loadImg(imgUrl: String) {
        loadImgUrl(imgUrl,binding.img, R.drawable.baseline_food_bank_24)
    }//TODO: std img

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
