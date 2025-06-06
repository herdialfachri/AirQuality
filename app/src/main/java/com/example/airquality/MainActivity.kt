package com.example.airquality

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private lateinit var pm1TextView: TextView
    private lateinit var pm25TextView: TextView
    private lateinit var pm10TextView: TextView
    private lateinit var statusKesehatanTextView: TextView

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
        pm1TextView = findViewById(R.id.dataPm1Tv)
        pm25TextView = findViewById(R.id.dataPm25Tv)
        pm10TextView = findViewById(R.id.dataPm10Tv)
        statusKesehatanTextView = findViewById(R.id.statusKesehatanTv)

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

                            pm1TextView.text = "${data.pm1_0} µg/m³"
                            pm25TextView.text = "${data.pm2_5} µg/m³"
                            pm10TextView.text = "${data.pm10} µg/m³"

                            // Kondisi jika semua PM melebihi 100
                            if (data.pm1_0 >= 150 && data.pm2_5 >= 150 && data.pm10 >= 150) {
                                // Sangat Buruk
                                statusKesehatanTextView.text = getString(R.string.bad)
                                statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_gas_24, 0)

                            } else if (data.pm1_0 >= 100 && data.pm2_5 >= 100 && data.pm10 >= 100) {
                                // Tidak Sehat
                                statusKesehatanTextView.text = getString(R.string.not_healthy)
                                statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_back_ios_new_24, 0)

                            } else if (data.pm1_0 >= 50 && data.pm2_5 >= 50 && data.pm10 >= 50) {
                                // Sedang
                                statusKesehatanTextView.text = getString(R.string.medium)
                                statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_news_24, 0)

                            } else {
                                // Sehat
                                statusKesehatanTextView.text = getString(R.string.healthy)
                                statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_smile_emoticon_24, 0)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    tempDataTextView.text = "Error: ${error.message}"
                    humiDataTextView.text = "Error"
                    co2DataTextView.text = "Error"
                    coDataTextView.text = "Error"
                    timestampTextView.text = "Error"
                    pm1TextView.text = "Error"
                    pm25TextView.text = "Error"
                    pm10TextView.text = "Error"
                }
            })

        // Navigasi ke NewsActivity
        toNewsActivity.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }
}