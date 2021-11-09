package com.example.locations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import com.google.gson.Gson
import java.io.InputStreamReader

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var citiesSpinner: Spinner
    lateinit var locationPrompt: EditText
    lateinit var showCities: Button
    private val gson = Gson()
    lateinit var cities: Cities
    private var chosenCityId: Long? = null
    private var thresholdDistance: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        citiesSpinner = findViewById(R.id.cities_dropdown)
        locationPrompt = findViewById(R.id.range_prompt)
        showCities = findViewById(R.id.show_cities)
        showCities.isEnabled = false
        locationPrompt.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("beforeText", s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                thresholdDistance = s.toString().toIntOrNull()
            }

            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    showCities.isEnabled = false
                    return
                }
                if (s.isEmpty()) {
                    showCities.isEnabled = false
                    return
                }
                if (s.toString().toIntOrNull()!! <= 0) {
                    showCities.isEnabled = false
                }
                showCities.isEnabled = true
            }
        })

        initSpinner()
    }

    fun initSpinner() {
        val cities_stream = resources.openRawResource(R.raw.cities)
        cities = gson.fromJson(InputStreamReader(cities_stream), Cities::class.java)
        citiesSpinner.adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, cities.cities.map { it -> it.name })
        citiesSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (thresholdDistance != null) {
            showCities.isEnabled = true
        }
        chosenCityId = id
        Log.d("chosen", id.toString())
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        showCities.isEnabled = false
        chosenCityId = null
        Toast.makeText(this, "Please, choose the city from the dropdown", Toast.LENGTH_LONG).show()
    }

    fun showCities(view: View) {
        Toast.makeText(this, thresholdDistance.toString(), Toast.LENGTH_LONG).show()
        val intent = Intent(this, CitiesActivity::class.java)
        intent.putExtra("chosen_city_id", chosenCityId)
        intent.putExtra("city_range", thresholdDistance)
        startActivity(intent)
    }
}