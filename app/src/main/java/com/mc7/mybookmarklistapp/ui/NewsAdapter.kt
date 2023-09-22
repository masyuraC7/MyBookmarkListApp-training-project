package com.mc7.mybookmarklistapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mc7.mybookmarklistapp.R
import com.mc7.mybookmarklistapp.data.local.entity.News
import com.mc7.mybookmarklistapp.databinding.ItemNewsBinding
import com.mc7.mybookmarklistapp.utils.DateFormatter

class NewsAdapter(private val onBookmarkClick: (News) -> Unit) :
    ListAdapter<News, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        holder.bind(news)

        val ivBookmark = holder.binding.ivBookmark
        if (news.isBookmarked) {
            ivBookmark.setImageDrawable(
                ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmarked_white))
        } else {
            ivBookmark.setImageDrawable(
                ContextCompat.getDrawable(ivBookmark.context, R.drawable.ic_bookmark_white))
        }

        ivBookmark.setOnClickListener {
            onBookmarkClick(news)
        }
    }

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(news: News) {
            binding.tvItemTitle.text = news.title
            binding.tvItemPublishedDate.text = DateFormatter.formatDate(news.publishedAt)

            Glide.with(itemView.context)
                .load(news.urlToImage)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                .into(binding.imgPoster)

            itemView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(news.url)
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<News> =
            object : DiffUtil.ItemCallback<News>() {
                override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                    return oldItem.title == newItem.title
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
                    return oldItem == newItem
                }
            }
    }
}