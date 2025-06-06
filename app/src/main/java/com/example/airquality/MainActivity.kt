package com.example.airquality

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.airquality.entity.AirQuality
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var toNewsActivity: ImageView
    private lateinit var tempDataTextView: TextView
    private lateinit var humiDataTextView: TextView
    private lateinit var co2DataTextView: TextView
    private lateinit var coDataTextView: TextView
    private lateinit var timestampTextView: TextView
    private lateinit var pm1TextView: TextView
    private lateinit var pm25TextView: TextView
    private lateinit var pm10TextView: TextView
    private lateinit var statusKesehatanTextView: TextView
    private lateinit var database: DatabaseReference

    private val CHANNEL_ID = "air_quality_channel"
    private var sudahNotifikasiBuruk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Minta izin notifikasi jika perlu (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

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

        // Buat notification channel
        createNotificationChannel()

        // Inisialisasi database Firebase
        database = FirebaseDatabase
            .getInstance("https://airquality-e6800-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("air_quality")

        // Ambil data terbaru
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

                            val pm1 = data.pm1_0
                            val pm25 = data.pm2_5
                            val pm10 = data.pm10

                            when {
                                pm1 >= 150 && pm25 >= 150 && pm10 >= 150 -> {
                                    statusKesehatanTextView.text = getString(R.string.bad)
                                    statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_gas_24, 0)

                                    if (!sudahNotifikasiBuruk) {
                                        tampilkanNotifikasiBuruk()
                                        sudahNotifikasiBuruk = true
                                    }
                                }
                                pm1 >= 100 && pm25 >= 100 && pm10 >= 100 -> {
                                    statusKesehatanTextView.text = getString(R.string.not_healthy)
                                    statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_back_ios_new_24, 0)
                                    sudahNotifikasiBuruk = false
                                }
                                pm1 >= 50 && pm25 >= 50 && pm10 >= 50 -> {
                                    statusKesehatanTextView.text = getString(R.string.medium)
                                    statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_news_24, 0)
                                    sudahNotifikasiBuruk = false
                                }
                                else -> {
                                    statusKesehatanTextView.text = getString(R.string.healthy)
                                    statusKesehatanTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_smile_emoticon_24, 0)
                                    sudahNotifikasiBuruk = false
                                }
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Air Quality Alert"
            val descriptionText = "Notifikasi kualitas udara buruk"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun tampilkanNotifikasiBuruk() {
        // Cek permission untuk notifikasi
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

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    // Opsional: handle jika user memberikan permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Izin diberikan
        }
    }
}
