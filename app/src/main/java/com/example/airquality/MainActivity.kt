package com.example.airquality

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.airquality.databinding.ActivityMainBinding
import com.example.airquality.entity.AirQuality
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private val CHANNEL_ID = "air_quality_channel"
    private var sudahNotifikasiBuruk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        createNotificationChannel()

        database = FirebaseDatabase
            .getInstance("https://airquality-e6800-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("air_quality")

        database.orderByChild("timestamp").limitToLast(1)
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val data = child.getValue(AirQuality::class.java)
                        if (data != null) {
                            with(binding) {
                                dataTempTv.text = "${data.temperature}°C"
                                dataHumiTv.text = "${data.humidity}%"
                                dataCo2Tv.text = "${data.co2_ppm} ppm"
                                dataCoTv.text = "${data.co_ppm} ppm"
                                dataTimeTv.text = data.timestamp
                                dataPm1Tv.text = "${data.pm1_0} µg/m³"
                                dataPm25Tv.text = "${data.pm2_5} µg/m³"
                                dataPm10Tv.text = "${data.pm10} µg/m³"

                                val pm1 = data.pm1_0
                                val pm25 = data.pm2_5
                                val pm10 = data.pm10

                                when {
                                    pm1 >= 150 && pm25 >= 150 && pm10 >= 150 -> {
                                        statusKesehatanTv.text = getString(R.string.bad)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_gas_24, 0)
                                        if (!sudahNotifikasiBuruk) {
                                            tampilkanNotifikasiBuruk()
                                            sudahNotifikasiBuruk = true
                                        }
                                    }
                                    pm1 >= 100 && pm25 >= 100 && pm10 >= 100 -> {
                                        statusKesehatanTv.text = getString(R.string.not_healthy)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_back_ios_new_24, 0)
                                        sudahNotifikasiBuruk = false
                                    }
                                    pm1 >= 50 && pm25 >= 50 && pm10 >= 50 -> {
                                        statusKesehatanTv.text = getString(R.string.medium)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_news_24, 0)
                                        sudahNotifikasiBuruk = false
                                    }
                                    else -> {
                                        statusKesehatanTv.text = getString(R.string.healthy)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_smile_emoticon_24, 0)
                                        sudahNotifikasiBuruk = false
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    with(binding) {
                        dataTempTv.text = "Error: ${error.message}"
                        dataHumiTv.text = "Error"
                        dataCo2Tv.text = "Error"
                        dataCoTv.text = "Error"
                        dataTimeTv.text = "Error"
                        dataPm1Tv.text = "Error"
                        dataPm25Tv.text = "Error"
                        dataPm10Tv.text = "Error"
                    }
                }
            })

        binding.toNewsBtn.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Air Quality Alert"
            val descriptionText = "Notifikasi kualitas udara buruk"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun tampilkanNotifikasiBuruk() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Status Udara Sangat Buruk!")
            .setContentText("Kadar PM sangat tinggi. Hindari aktivitas di luar ruangan.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(1, builder.build())
    }
}
