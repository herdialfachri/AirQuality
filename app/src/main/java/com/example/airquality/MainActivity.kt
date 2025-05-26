package com.example.airquality

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var toNewsActivity: ImageView
    private lateinit var tempDataTextView: TextView
    private lateinit var humiDataTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toNewsActivity = findViewById(R.id.toNewsBtn)
        tempDataTextView = findViewById(R.id.dataTempTv)
        humiDataTextView = findViewById(R.id.dataHumiTv)

        // Inisialisasi referensi ke database
        database = FirebaseDatabase.getInstance("https://airquality-e6800-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("air_quality")

        // Ambil data dari Firebase
        database.addValueEventListener(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.child("temperature").getValue(Double::class.java)
                val humidity = snapshot.child("humidity").getValue(Double::class.java)
                val timestamp = snapshot.child("timestamp").getValue(String::class.java)

                tempDataTextView.text = "${temperature ?: "-"}°C"
                humiDataTextView.text = "${humidity ?: "-"}%"
            }

            override fun onCancelled(error: DatabaseError) {
                tempDataTextView.text = "Gagal memuat data: ${error.message}"
                humiDataTextView.text = "Gagal memuat data: ${error.message}"
            }
        })

        toNewsActivity.setOnClickListener {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
    }
}