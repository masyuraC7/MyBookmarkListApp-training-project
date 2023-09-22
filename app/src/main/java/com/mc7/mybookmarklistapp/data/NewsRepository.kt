package com.mc7.mybookmarklistapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.mc7.mybookmarklistapp.BuildConfig
import com.mc7.mybookmarklistapp.data.local.entity.News
import com.mc7.mybookmarklistapp.data.local.room.NewsDao
import com.mc7.mybookmarklistapp.data.remote.response.NewsResponse
import com.mc7.mybookmarklistapp.data.remote.retrofit.ApiService
import com.mc7.mybookmarklistapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository private constructor(
    private val apiService: ApiService,
    private val newsDao: NewsDao,
    private val appExecutors: AppExecutors
) {
    private val result = MediatorLiveData<Result<List<News>>>()

    fun getHeadlineNews(): LiveData<Result<List<News>>> {
        result.value = Result.Loading
        val client = apiService.getNews(BuildConfig.apiKey)

        client.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.articles
                    val newsList = ArrayList<News>()

                    appExecutors.diskIO.execute {
                        articles?.forEach { article ->
                            val isBookmarked = newsDao.isNewsBookmarked(article.title)
                            val news = News(
                                article.title,
                                article.publishedAt,
                                article.urlToImage,
                                article.url,
                                isBookmarked
                            )

                            newsList.add(news)
                        }

                        newsDao.deleteAll()
                        newsDao.insertNews(newsList)
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        val localData = newsDao.getNews()

        result.addSource(localData) { newData: List<News> ->
            result.value = Result.Success(newData)
        }

        return result
    }

    fun getBookmarkedNews(): LiveData<List<News>> {
        return newsDao.getBookmarkedNews()
    }

    fun setBookmarkedNews(news: News, bookmarkState: Boolean) {
        appExecutors.diskIO.execute {
            news.isBookmarked = bookmarkState
            newsDao.updateNews(news)
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: NewsDao,
            appExecutors: AppExecutors
        ): NewsRepository =
            instance ?: synchronized(this) {
                instance
                    ?: NewsRepository(
                        apiService,
                        newsDao,
                        appExecutors
                    )
            }.also { instance = it }
    }
}