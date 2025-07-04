package com.example.airquality

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.airquality.databinding.ActivityMainBinding
import com.example.airquality.entity.AirQuality
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private val CHANNEL_ID = "air_quality_channel"
    private var sudahNotifikasiBaik = false
    private var sudahNotifikasiSedang = false
    private var sudahNotifikasiBahaya = false
    private var sudahNotifikasiTidakSehat = false

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
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_sentiment_very_dissatisfied_24, 0)
                                        if (!sudahNotifikasiBahaya) {
                                            tampilkanNotifikasiBuruk()
                                            sudahNotifikasiBahaya = true
                                            sudahNotifikasiTidakSehat = false
                                            sudahNotifikasiSedang = false
                                            sudahNotifikasiBaik = false
                                        }
                                    }
                                    pm1 >= 100 && pm25 >= 100 && pm10 >= 100 -> {
                                        statusKesehatanTv.text = getString(R.string.not_healthy)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_sentiment_dissatisfied_24, 0)
                                        if (!sudahNotifikasiTidakSehat) {
                                            tampilkanNotifikasiTidakSehat()
                                            sudahNotifikasiTidakSehat = true
                                            sudahNotifikasiBahaya = false
                                            sudahNotifikasiSedang = false
                                            sudahNotifikasiBaik = false
                                        }
                                    }
                                    pm1 >= 50 && pm25 >= 50 && pm10 >= 50 -> {
                                        statusKesehatanTv.text = getString(R.string.medium)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_sentiment_neutral_24, 0)
                                        if (!sudahNotifikasiSedang) {
                                            tampilkanNotifikasiSedang()
                                            sudahNotifikasiSedang = true
                                            sudahNotifikasiBahaya = false
                                            sudahNotifikasiTidakSehat = false
                                            sudahNotifikasiBaik = false
                                        }
                                    }
                                    else -> {
                                        statusKesehatanTv.text = getString(R.string.healthy)
                                        statusKesehatanTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_sentiment_satisfied_alt_24, 0)
                                        if (!sudahNotifikasiBaik) {
                                            tampilkanNotifikasiBaik()
                                            sudahNotifikasiBaik = true
                                            sudahNotifikasiBahaya = false
                                            sudahNotifikasiTidakSehat = false
                                            sudahNotifikasiSedang = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    tampilkanError()
                }
            })

        tampilkanChartPM1danPM25()

        binding.toNewsBtn.setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
    }

    private fun tampilkanChartPM1danPM25() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val displayFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val sevenDaysAgo = calendar.time

        val pm1Map = sortedMapOf<String, MutableList<Int>>()
        val pm25Map = sortedMapOf<String, MutableList<Int>>()

        database.orderByChild("timestamp").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pm1Map.clear()
                pm25Map.clear()

                for (child in snapshot.children) {
                    val data = child.getValue(AirQuality::class.java) ?: continue
                    try {
                        val date = dateFormat.parse(data.timestamp) ?: continue
                        if (date.before(sevenDaysAgo)) continue

                        val labelTanggal = displayFormat.format(date) // contoh: "02 Jul"
                        pm1Map.getOrPut(labelTanggal) { mutableListOf() }.add(data.pm1_0)
                        pm25Map.getOrPut(labelTanggal) { mutableListOf() }.add(data.pm2_5)
                    } catch (_: Exception) {}
                }

                val pm1Entries = ArrayList<BarEntry>()
                val pm25Entries = ArrayList<BarEntry>()
                val tanggalList = pm1Map.keys.toList()  // dipastikan urut

                for ((index, tanggal) in tanggalList.withIndex()) {
                    val rataPM1 = pm1Map[tanggal]?.average()?.toFloat() ?: 0f
                    val rataPM25 = pm25Map[tanggal]?.average()?.toFloat() ?: 0f
                    pm1Entries.add(BarEntry(index.toFloat(), rataPM1))
                    pm25Entries.add(BarEntry(index.toFloat(), rataPM25))
                }

                // Set chart PM1
                val pm1DataSet = BarDataSet(pm1Entries, "Rata-rata PM1 (µg/m³)")
                pm1DataSet.color = resources.getColor(R.color.dark_slate_blue, null)
                binding.pm1Chart.data = BarData(pm1DataSet)
                binding.pm1Chart.xAxis.valueFormatter = IndexAxisValueFormatter(tanggalList)
                binding.pm1Chart.xAxis.granularity = 1f
                binding.pm1Chart.xAxis.labelRotationAngle = -45f
                binding.pm1Chart.description = Description().apply { text = "PM1 - 7 Hari Terakhir" }
                binding.pm1Chart.animateY(1000)
                binding.pm1Chart.invalidate()

                // Set chart PM2.5
                val pm25DataSet = BarDataSet(pm25Entries, "Rata-rata PM2.5 (µg/m³)")
                pm25DataSet.color = resources.getColor(R.color.medium_orchid, null)
                binding.pm25Chart.data = BarData(pm25DataSet)
                binding.pm25Chart.xAxis.valueFormatter = IndexAxisValueFormatter(tanggalList)
                binding.pm25Chart.xAxis.granularity = 1f
                binding.pm25Chart.xAxis.labelRotationAngle = -45f
                binding.pm25Chart.description = Description().apply { text = "PM2.5 - 7 Hari Terakhir" }
                binding.pm25Chart.animateY(1000)
                binding.pm25Chart.invalidate()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun tampilkanError() {
        with(binding) {
            val errorMsg = "Terdapat kesalahan"
            dataTempTv.text = errorMsg
            dataHumiTv.text = errorMsg
            dataCo2Tv.text = errorMsg
            dataCoTv.text = errorMsg
            dataTimeTv.text = errorMsg
            dataPm1Tv.text = errorMsg
            dataPm25Tv.text = errorMsg
            dataPm10Tv.text = errorMsg
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

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun tampilkanNotifikasiBaik() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Udara Baik")
            .setContentText("Kualitas udara dalam kondisi baik. Tetap jaga kesehatan!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(2, builder.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun tampilkanNotifikasiSedang() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Udara Sedang")
            .setContentText("Kualitas udara sedang. Waspadai perubahan cuaca.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(3, builder.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun tampilkanNotifikasiTidakSehat() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Udara Tidak Sehat")
            .setContentText("Udara tidak sehat. Gunakan masker saat keluar rumah.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(4, builder.build())
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun tampilkanNotifikasiBuruk() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Udara Berbahaya")
            .setContentText("Kualitas udara sangat buruk. Segera hindari aktivitas luar ruangan!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(this).notify(5, builder.build())
    }
}
