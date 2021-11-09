package com.example.locations

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import java.io.InputStreamReader

class CitiesActivity : AppCompatActivity() {

    lateinit var cities: Cities
    private val gson = Gson()
    lateinit var citiesList: ListView
    lateinit var goBackBtn: Button
    lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cities)

        citiesList = findViewById(R.id.cities_list)
        goBackBtn = findViewById(R.id.go_back_btn)
        emptyText = findViewById(R.id.empty_list_text)
        citiesList.visibility = View.VISIBLE
        emptyText.visibility = View.GONE

        initCities()
        calculateCities()
    }

    fun initCities() {
        val cities_stream = resources.openRawResource(R.raw.cities)
        cities = gson.fromJson(InputStreamReader(cities_stream), Cities::class.java)
    }

    fun calculateCities() {
        val cityId: Long = intent.getLongExtra("chosen_city_id", -1)
        val range: Int = intent.getIntExtra("city_range", -1)
        if (cityId < 0) {
            Toast.makeText(this, "Seems like you havent chosen any city!", Toast.LENGTH_LONG).show()
            return
        }
        if (range < 0) {
            Toast.makeText(this, "Seems like you havent range of the detection!", Toast.LENGTH_LONG).show()
            return
        }

        val targetCityLocation = Location("targetCity")
        val destination = Location("destination")
        val targetCity = cities.cities.find { it -> it.id.toLong() == cityId }
        if (targetCity != null) {
            targetCityLocation.longitude = targetCity.coord.lon
            targetCityLocation.latitude = targetCity.coord.lat
        }

        val citiesNames: ArrayList<String> = ArrayList()
        for (city in cities.cities) {
            destination.longitude = city.coord.lon
            destination.latitude = city.coord.lat

            if (targetCityLocation.distanceTo(destination) / 1000 < range) {
                citiesNames.add(city.name)
            }
        }
        citiesList.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, citiesNames)

        if (citiesNames.size == 0) {
            emptyText.visibility = View.VISIBLE
            citiesList.visibility = View.GONE
        }
    }

    fun goBack(view: View) {
        this.finish()
        return
    }
}