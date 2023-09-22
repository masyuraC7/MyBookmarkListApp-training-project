package com.mc7.mybookmarklistapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.mc7.mybookmarklistapp.data.NewsRepository
import com.mc7.mybookmarklistapp.data.local.entity.News

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {

    fun getHeadlineNews() = newsRepository.getHeadlineNews()

    fun getBookmarkedNews() = newsRepository.getBookmarkedNews()

    fun saveNews(news: News) {
        newsRepository.setBookmarkedNews(news, true)
    }

    fun deleteNews(news: News) {
        newsRepository.setBookmarkedNews(news, false)
    }

}