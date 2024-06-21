package com.example.cateringapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cateringapp.R
import com.example.cateringapp.databinding.ItemNewsBinding
import com.example.cateringapp.remote.res.NewsRes
import com.example.cateringapp.utils.loadImgUrl

class NewsAdapter(
    private val listener: OnItemClickListener
) :  ListAdapter<NewsRes, NewsAdapter.NewsAdapterViewHolder>(NEWS_DIFF_UTIL){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterViewHolder {
        val binding = ItemNewsBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsAdapterViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root){


        fun bind(newsItem: NewsRes){
            binding.tvTitle.text = newsItem.title
            binding.tvDesc.text = newsItem.text
            loadImgUrl(newsItem.imgUrl,binding.img, R.drawable.baseline_food_bank_24)

            binding.root.setOnClickListener {
                listener.onItemClick(newsItem)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(newsItem: NewsRes)
    }

    companion object {
        private val NEWS_DIFF_UTIL =object : DiffUtil.ItemCallback<NewsRes>() {
            override fun areItemsTheSame(oldItem: NewsRes, newItem: NewsRes): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: NewsRes, newItem: NewsRes): Boolean =
                oldItem == newItem
        }
    }
}

