package com.example.airquality

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailNewsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)

        backButton = findViewById(R.id.arrowBackBtnDetail)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val judul = intent.getStringExtra("judul")
        val deskripsi = intent.getStringExtra("deskripsi")
        val gambar = intent.getStringExtra("gambar")
        val sumber = intent.getStringExtra("sumber")
        val pengupload = intent.getStringExtra("pengupload")
        val tanggal = intent.getLongExtra("tanggal", 0L)

        val imgBerita: ImageView = findViewById(R.id.imageViewBerita)
        val tvJudul: TextView = findViewById(R.id.textViewJudul)
        val tvTanggal: TextView = findViewById(R.id.textViewTanggal)
        val tvDeskripsi: TextView = findViewById(R.id.textViewDeskripsi)
        val tvSumber: TextView = findViewById(R.id.textViewSumber)
        val tvPengupload: TextView = findViewById(R.id.textViewPengupload)

        tvJudul.text = judul
        tvDeskripsi.text = deskripsi?.replace("\\n", "\n")
        tvSumber.text = "Sumber: $sumber"
        tvPengupload.text = "Penulis: $pengupload"
        tvTanggal.text = formatTimestamp(tanggal)

        Glide.with(this).load(gambar).into(imgBerita)
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
}