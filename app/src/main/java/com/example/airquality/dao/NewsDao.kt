package com.example.airquality.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.airquality.entity.NewsEntity

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Query("SELECT * FROM berita ORDER BY tanggalUpload DESC")
    fun getAllNews(): LiveData<List<NewsEntity>>

    @Query("SELECT * FROM berita WHERE id = :newsId")
    suspend fun getNewsById(newsId: Int): NewsEntity?

    @Insert
    fun insertAll(newsList: List<NewsEntity>)

    @Delete
    suspend fun deleteNews(news: NewsEntity)

    @Update
    suspend fun updateNews(news: NewsEntity)
}