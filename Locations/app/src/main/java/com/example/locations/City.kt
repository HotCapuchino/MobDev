package com.example.locations

data class Cities(val cities: Array<City>)

data class City(val id: Int, val name: String, val state: String, val country: String, val coord: Coordinates)

data class Coordinates(val lon: Double, val lat: Double)