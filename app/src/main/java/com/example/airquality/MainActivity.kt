package com.example.airquality

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var toNewsActivity: Button
    private lateinit var dataTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toNewsActivity = findViewById(R.id.toNewsBtn)
        dataTextView = findViewById(R.id.dataTextView)

        // Inisialisasi referensi ke database
        database = FirebaseDatabase.getInstance("https://airquality-e6800-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("air_quality")

        // Ambil data dari Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.child("temperature").getValue(Double::class.java)
                val humidity = snapshot.child("humidity").getValue(Double::class.java)
                val timestamp = snapshot.child("timestamp").getValue(String::class.java)

                dataTextView.text = """
                    Suhu: ${temperature ?: "-"}°C
                    Kelembapan: ${humidity ?: "-"}%
                    Waktu: ${timestamp ?: "-"}
                """.trimIndent()
            }

            override fun onCancelled(error: DatabaseError) {
                dataTextView.text = "Gagal memuat data: ${error.message}"
            }
        })

        toNewsActivity.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
    }
}