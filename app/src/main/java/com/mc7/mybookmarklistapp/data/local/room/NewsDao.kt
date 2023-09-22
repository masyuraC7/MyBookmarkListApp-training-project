package com.mc7.mybookmarklistapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mc7.mybookmarklistapp.data.local.entity.News

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY publishedAt DESC")
    fun getNews(): LiveData<List<News>>

    @Query("SELECT * FROM news where bookmarked = 1")
    fun getBookmarkedNews(): LiveData<List<News>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNews(news: List<News>)

    @Update
    fun updateNews(news: News)

    @Query("DELETE FROM news WHERE bookmarked = 0")
    fun deleteAll()

    @Query("SELECT EXISTS(SELECT * FROM news WHERE title = :title AND bookmarked = 1)")
    fun isNewsBookmarked(title: String): Boolean
}