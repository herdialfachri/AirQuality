package com.example.airquality.entity

data class AirQuality(
    val temperature: Double = 0.0,
    val humidity: Double = 0.0,
    val co2_ppm: Int = 0,
    val co_ppm: Int = 0,
    val timestamp: String = "0"
)