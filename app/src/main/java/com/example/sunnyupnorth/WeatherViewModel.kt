package com.example.sunnyupnorth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory



open class WeatherViewModel : ViewModel() {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val api = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(WeatherApi::class.java)

    var searchLocation by mutableStateOf("")
        private set

    fun onSearchChange(newSearchLocation: String) {
        searchLocation = newSearchLocation
    }

    var weather by mutableStateOf<WeatherResponse?>(null)

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getWeather(city, "24e6ac99e599996103979747b1734d33")
                }
                weather = response
            } catch (e: Exception) {
                e.printStackTrace()
                weather = null
            }
        }
    }


}
