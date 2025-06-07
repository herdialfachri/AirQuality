package com.example.airquality.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.airquality.R
import com.example.airquality.entity.NewsEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewsAdapter(
    private val newsList: List<NewsEntity>,
    private val onItemClick: (NewsEntity) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textJudul: TextView = view.findViewById(R.id.textJudul)
        val textTanggal: TextView = view.findViewById(R.id.textTanggal)
        val textPengupload: TextView = view.findViewById(R.id.textPengupload)
        val imageBerita: ImageView = view.findViewById(R.id.newsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.textJudul.text = news.judul
        holder.textPengupload.text = "Diupload oleh: ${news.pengupload}"
        holder.textTanggal.text = formatTimestamp(news.tanggalUpload)

        Glide.with(holder.itemView.context)
            .load(news.gambar)
            .placeholder(R.color.gray)
            .into(holder.imageBerita)

        holder.imageBerita.contentDescription = "Gambar berita: ${news.judul}"

        holder.itemView.setOnClickListener {
            onItemClick(news)
        }
    }

    override fun getItemCount(): Int = newsList.size

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date(timestamp))
    }
}