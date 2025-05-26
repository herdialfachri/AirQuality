package com.example.airquality.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "berita")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val gambar: String,
    val sumber: String,
    val deskripsi: String,
    val tanggalUpload: Long,
    val pengupload: String
)