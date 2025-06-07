package com.example.airquality.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airquality.R
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
                                            judul = context.getString(R.string.news1_title),
                                            gambar = "https://lifepack.id/wp-content/uploads/2020/05/paru_paru_basah-768x512.jpg",
                                            sumber = context.getString(R.string.news1_source),
                                            deskripsi = context.getString(R.string.news1_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news2_title),
                                            gambar = "https://www.ruangenergi.com/wp-content/uploads/2023/09/63ec3f510da07.jpg",
                                            sumber = context.getString(R.string.news2_source),
                                            deskripsi = context.getString(R.string.news2_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news3_title),
                                            gambar = "https://tugubandung.id/wp-content/uploads/2023/08/DUA-pohon-karet-kebo-yang-sudah-sangat-tua-dan-sangat-tinggi-di-Selabintana-Kabupaten-Sukabumi.-Foto-Widodo-A-scaled-1.jpg",
                                            sumber = context.getString(R.string.news3_source),
                                            deskripsi = context.getString(R.string.news3_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news4_title),
                                            gambar = "https://dvgddkosknh6r.cloudfront.net/live/media/img/1655365189-IMG-20220616-WA0008.jpg",
                                            sumber = context.getString(R.string.news4_source),
                                            deskripsi = context.getString(R.string.news4_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news6_title),
                                            gambar = "https://image.sggp.org.vn/w1000/Uploaded/2024/negesfjozly/2023_12_16/p4c-3839jpg-5461.jpg",
                                            sumber = context.getString(R.string.news6_source),
                                            deskripsi = context.getString(R.string.news6_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news7_title),
                                            gambar = "https://lh5.googleusercontent.com/Unq1_cfyWqlDTxZIfEHde2MU_GJm9G_YgH2d9pTXM75EEHxoIukBFcS9LNfZHknich4gkwomRVuL5OSwQ4eJWY_IfwsE94UbYrdR7_lOXZkAJciKFGjXA-2VNOc2hbm_Oftlr-s",
                                            sumber = context.getString(R.string.news7_source),
                                            deskripsi = context.getString(R.string.news7_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news8_title),
                                            gambar = "https://statik.tempo.co/data/2020/11/24/id_982822/982822_720.jpg",
                                            sumber = context.getString(R.string.news8_source),
                                            deskripsi = context.getString(R.string.news8_desc),
                                            tanggalUpload = System.currentTimeMillis(),
                                            pengupload = "Admin"
                                        ),
                                        NewsEntity(
                                            judul = context.getString(R.string.news5_title),
                                            gambar = "https://assets.pikiran-rakyat.com/crop/0x0:0x0/720x0/webp/photo/2025/04/19/2490729177.jpg",
                                            sumber = context.getString(R.string.news5_source),
                                            deskripsi = context.getString(R.string.news5_desc),
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