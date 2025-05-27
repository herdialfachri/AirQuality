package com.example.airquality

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.airquality.entity.AirQuality
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var toNewsActivity: ImageView
    private lateinit var tempDataTextView: TextView
    private lateinit var humiDataTextView: TextView
    private lateinit var co2DataTextView: TextView
    private lateinit var coDataTextView: TextView
    private lateinit var timestampTextView: TextView
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi tampilan
        toNewsActivity = findViewById(R.id.toNewsBtn)
        tempDataTextView = findViewById(R.id.dataTempTv)
        humiDataTextView = findViewById(R.id.dataHumiTv)
        co2DataTextView = findViewById(R.id.dataCo2Tv)
        coDataTextView = findViewById(R.id.dataCoTv)
        timestampTextView = findViewById(R.id.dataTimeTv)

        // Inisialisasi database Firebase
        database = FirebaseDatabase.getInstance("https://airquality-e6800-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("air_quality")

        // Ambil data terbaru (dengan orderByChild + limitToLast)
        database.orderByChild("timestamp").limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val data = child.getValue(AirQuality::class.java)
                        if (data != null) {
                            tempDataTextView.text = "${data.temperature}°C"
                            humiDataTextView.text = "${data.humidity}%"
                            co2DataTextView.text = "${data.co2_ppm} ppm"
                            coDataTextView.text = "${data.co_ppm} ppm"
                            timestampTextView.text = data.timestamp
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    tempDataTextView.text = "Error: ${error.message}"
                    humiDataTextView.text = "Error"
                    co2DataTextView.text = "Error"
                    coDataTextView.text = "Error"
                    timestampTextView.text = "Error"
                }
            })

        // Navigasi ke NewsActivity
        toNewsActivity.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }
}