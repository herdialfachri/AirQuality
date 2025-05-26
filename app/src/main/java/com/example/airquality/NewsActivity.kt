package com.example.airquality

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.airquality.adapter.NewsAdapter
import com.example.airquality.dao.NewsDao
import com.example.airquality.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var newsDao: NewsDao
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        recyclerView = findViewById(R.id.recyclerViewNews)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "news_database"
        ).build()

        newsDao = db.newsDao()

//        insertDummyDataIfNeeded()
        loadNews()
    }

//    private fun insertDummyDataIfNeeded() {
//        lifecycleScope.launch {
//            val count = withContext(Dispatchers.IO) {
//                newsDao.getAllNews().size
//            }
//
//            if (count == 0) {
//                val dummyNews = listOf(
//                    NewsEntity(
//                        judul = "Udara Jakarta Membaik",
//                        gambar = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Smog_in_Santiago.jpg/800px-Smog_in_Santiago.jpg",
//                        sumber = "Kompas",
//                        deskripsi = "Pemerintah DKI mulai menerapkan kebijakan uji emisi massal.",
//                        tanggalUpload = System.currentTimeMillis(),
//                        pengupload = "Admin"
//                    ),
//                    NewsEntity(
//                        judul = "Tips Mengurangi Polusi Rumah Tangga",
//                        gambar = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Smog_in_Santiago.jpg/800px-Smog_in_Santiago.jpg",
//                        sumber = "Detik",
//                        deskripsi = "Hindari pembakaran sampah dan gunakan produk ramah lingkungan.",
//                        tanggalUpload = System.currentTimeMillis(),
//                        pengupload = "Admin"
//                    ),
//                    NewsEntity(
//                        judul = "Pengaruh Polusi Terhadap Kesehatan Anak",
//                        gambar = "https://example.com/image3.jpg",
//                        sumber = "CNN Indonesia",
//                        deskripsi = "Paparan jangka panjang bisa mengganggu perkembangan paru-paru anak.",
//                        tanggalUpload = System.currentTimeMillis(),
//                        pengupload = "Admin"
//                    ),
//                    NewsEntity(
//                        judul = "Jakarta Terapkan Sistem Genap Ganjil Ekstra",
//                        gambar = "https://example.com/image4.jpg",
//                        sumber = "Liputan6",
//                        deskripsi = "Langkah ini diambil untuk mengurangi emisi kendaraan bermotor.",
//                        tanggalUpload = System.currentTimeMillis(),
//                        pengupload = "Admin"
//                    ),
//                    NewsEntity(
//                        judul = "Inovasi Filter Udara untuk Kota Besar",
//                        gambar = "https://example.com/image5.jpg",
//                        sumber = "Tech News",
//                        deskripsi = "Start-up lokal mengembangkan teknologi filter udara ramah lingkungan.",
//                        tanggalUpload = System.currentTimeMillis(),
//                        pengupload = "Admin"
//                    )
//                )
//
//                withContext(Dispatchers.IO) {
//                    newsDao.insertAll(dummyNews)
//                }
//
//                loadNews()
//            }
//        }
//    }

    private fun loadNews() {
        lifecycleScope.launch {
            val newsList = withContext(Dispatchers.IO) {
                newsDao.getAllNews()
            }

            recyclerView.adapter = NewsAdapter(newsList) { news ->
                val intent = Intent(this@NewsActivity, DetailNewsActivity::class.java).apply {
                    putExtra("judul", news.judul)
                    putExtra("deskripsi", news.deskripsi)
                    putExtra("gambar", news.gambar)
                    putExtra("tanggal", news.tanggalUpload)
                    putExtra("sumber", news.sumber)
                    putExtra("pengupload", news.pengupload)
                }
                startActivity(intent)
            }
        }
    }
}