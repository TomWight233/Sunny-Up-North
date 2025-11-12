package com.example.sunnyupnorth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class WeatherViewModel : ViewModel() {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    var searchLocation by mutableStateOf("")
        private set

    fun onSearchChange(newSearchLocation: String) {
        searchLocation = newSearchLocation
    }

    var weather by mutableStateOf<WeatherResponse?>(null)
        private set

    suspend fun fetchWeather(city: String) {
        weather = api.getWeather(city, "24e6ac99e599996103979747b1734d33")
    }


}
