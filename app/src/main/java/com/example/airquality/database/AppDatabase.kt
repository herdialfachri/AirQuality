package com.example.airquality.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.airquality.dao.NewsDao
import com.example.airquality.entity.NewsEntity

@Database(entities = [NewsEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}