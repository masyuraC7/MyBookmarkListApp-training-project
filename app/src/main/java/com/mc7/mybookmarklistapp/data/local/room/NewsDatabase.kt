package com.mc7.mybookmarklistapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mc7.mybookmarklistapp.data.local.entity.News

@Database(entities = [News::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var instance: NewsDatabase? = null
        fun getInstance(context: Context): NewsDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java, "News.db"
                ).build()
            }
    }
}