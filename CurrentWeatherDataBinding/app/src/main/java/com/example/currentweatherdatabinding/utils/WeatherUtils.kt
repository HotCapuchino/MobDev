package com.example.currentweatherdatabinding.utils

import android.annotation.SuppressLint
import com.example.currentweatherdatabinding.R
import com.example.currentweatherdatabinding.dataclasses.MainWeather
import com.example.currentweatherdatabinding.dataclasses.WeatherData
import com.example.currentweatherdatabinding.dataclasses.Wind
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class WeatherUtils {
    companion object {

        @SuppressLint("SimpleDateFormat")
        val formatter = SimpleDateFormat("dd MM yyyy HH:mm")
        private val gson = Gson()

        fun loadWeather(cityName: String, API_KEY: String): WeatherData? {
            val weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=${cityName}&appid=${API_KEY}&units=metric&lang=ru";
            try {
                val stream = URL(weatherURL).getContent() as InputStream
                val data = JSONTokener(Scanner(stream).nextLine()).nextValue() as JSONObject

                if (data.get("cod").toString().toInt() >= 400) {
                    // smth went wrong
                }

                val main = gson.fromJson(data.get("main").toString(), MainWeather::class.java)
                val speed = (data.get("wind") as JSONObject).get("speed").toString().toDouble()
                val windDirection = defineWindDirection((data.get("wind") as JSONObject).get("deg").toString().toInt())
                val wind = Wind(speed, windDirection)
                val description = ((data.get("weather") as JSONArray)[0] as JSONObject).get("description").toString()
                val date = formatter.format(Date(Timestamp(data.get("dt").toString().toLong()).time * 1000))

                return WeatherData(description, main, wind, date)
            } catch (e: IOException) {
                //
            }
            return null
        }

        private fun defineWindDirection(azimut: Int): String {
            val azimuts = intArrayOf(0, 45, 90, 135, 180, 225, 270, 315, 359)
            for (i in azimuts.indices) {
                azimuts[i] = abs(azimuts[i] - azimut)
            }
            var minDir = 360
            var minIndex = 0
            for (i in azimuts.indices) {
                if (azimuts[i] < minDir) {
                    minDir = azimuts[i]
                    minIndex = i
                }
            }
            return when (minIndex) {
                0 -> "c"
                1 -> "c/в"
                2 -> "в"
                3 -> "ю/в"
                4 -> "ю"
                5 -> "ю/з"
                6 -> "з"
                7 -> "с/з"
                8 -> "c"
                else -> "c"
            }
        }
    }
}