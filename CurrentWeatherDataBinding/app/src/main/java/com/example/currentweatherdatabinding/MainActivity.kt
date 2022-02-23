package com.example.currentweatherdatabinding

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.currentweatherdatabinding.databinding.ActivityMainBinding
import com.example.currentweatherdatabinding.service.FetchingWeatherEvent
import com.example.currentweatherdatabinding.service.WeatherFetcherService
import com.example.currentweatherdatabinding.utils.WeatherUtils
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity(),  AdapterView.OnItemSelectedListener{

    lateinit var spinner: Spinner
    lateinit var cityNameView: TextView
    lateinit var dBind: ActivityMainBinding
    @SuppressLint("SimpleDateFormat")
    lateinit var API_KEY: String
    private var cityName = ""
    lateinit var weatherFetcherService: WeatherFetcherService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dBind = DataBindingUtil.setContentView(this, R.layout.activity_main)

        spinner = findViewById(R.id.cities_dropdown)
        cityNameView = findViewById(R.id.city_name)
        API_KEY = getString(R.string.API_KEY)

        weatherFetcherService = WeatherFetcherService()
        initSpinner()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        startFetchingWeather()
    }

    override fun onRestart() {
        super.onRestart()
        startFetchingWeather()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    private fun initSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.cities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            cityName = parent.getItemAtPosition(position).toString()
            cityNameView.text = cityName
        }

        startFetchingWeather()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // nothing changes
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onUpdateClick(v: View) {
        GlobalScope.launch(Dispatchers.IO) {
            dBind.weatherData = WeatherUtils.loadWeather(cityName, API_KEY)
        }
    }

    private fun startFetchingWeather() {
        Intent(this, WeatherFetcherService::class.java).also { intent ->
            intent.putExtra("cityName", cityName)
            intent.putExtra("API_KEY", API_KEY)
            startService(intent)
        }
    }

    @Subscribe
    fun onFetchingWeatherEvent(fetchingWeatherEvent: FetchingWeatherEvent) {
        dBind.weatherData = fetchingWeatherEvent.weatherData
    }
}