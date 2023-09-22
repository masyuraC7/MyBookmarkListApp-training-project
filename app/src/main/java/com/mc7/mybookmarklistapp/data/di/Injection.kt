package com.mc7.mybookmarklistapp.data.di

import android.content.Context
import com.mc7.mybookmarklistapp.data.NewsRepository
import com.mc7.mybookmarklistapp.data.local.room.NewsDatabase
import com.mc7.mybookmarklistapp.data.remote.retrofit.ApiConfig
import com.mc7.mybookmarklistapp.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): NewsRepository {
        val apiService = ApiConfig.getApiService()
        val database = NewsDatabase.getInstance(context)
        val dao = database.newsDao()
        val appExecutors = AppExecutors()

        return NewsRepository.getInstance(apiService, dao, appExecutors)
    }
}