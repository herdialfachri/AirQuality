package com.example.airquality

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.airquality.adapter.NewsAdapter
import com.example.airquality.dao.NewsDao
import com.example.airquality.database.AppDatabase
import com.example.airquality.entity.NewsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var newsDao: NewsDao
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        backButton = findViewById(R.id.arrowBackBtn)
        backButton.setOnClickListener {
            onBackPressed()
        }

        recyclerView = findViewById(R.id.recyclerViewNews)
        db = AppDatabase.getDatabase(applicationContext)
        newsDao = db.newsDao()

        observeNews()
    }

    private fun observeNews() {
        newsDao.getAllNews().observe(this) { newsList ->
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