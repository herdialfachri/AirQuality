package com.example.airquality.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airquality.dao.NewsDao
import com.example.airquality.entity.NewsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [NewsEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "airquality_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // Masukkan data awal
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.newsDao()?.insertAll(
                                    listOf(
                                        NewsEntity(
                                            judul = "Polusi Udara di Kota X Meningkat",
                                            gambar = "https://asset.kompas.com/crops/GRJOHlW1hFZgwvr2Ivt83AqRjsw=/102x0:902x533/750x500/data/photo/2021/07/28/610112d4e1a5c.jpg",
                                            sumber = "Detik.com",
                                            deskripsi = "Tingkat polusi udara mengalami peningkatan drastis...",
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = "Tips Menjaga Kesehatan Paru-paru",
                                            gambar = "https://asset.kompas.com/crops/GRJOHlW1hFZgwvr2Ivt83AqRjsw=/102x0:902x533/750x500/data/photo/2021/07/28/610112d4e1a5c.jpg",
                                            sumber = "Kompas.com",
                                            deskripsi = "Berikut adalah tips menjaga kesehatan paru-paru...",
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        )
                                    )
                                )
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}