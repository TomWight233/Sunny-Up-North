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

    fun onSearchChange(newSearchLocation: String) {
        searchLocation = newSearchLocation
    }

    var weather by mutableStateOf<WeatherResponse?>(null)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var saveMessage by mutableStateOf<String?>(null)
        private set

    var resetMessage by mutableStateOf<String?>(null)
        private set

    public var savedLocations = MutableList(10) { "" }

    var counter = 0

    fun saveLocation(city: String) {
        if (city !in savedLocations) {
            savedLocations[counter] = city
            saveMessage = "Location saved!"
            counter += 1
        }
        else {
            saveMessage = "Already saved."
        }

        viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            saveMessage = null
        }
    }

    fun clearLocations(){
        savedLocations = MutableList(10) { "" }
        counter = 0
        resetMessage = "Clear Successful"
        viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            resetMessage = null
        }
    }

    val apiKey = BuildConfig.MY_API_KEY

    fun fetchWeather(city: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getWeather(city, apiKey)
                }
                weather = response
                errorMessage = null
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Location not found. Please try again."
                onResult(false)
                weather = null
            }
        }
    }


}
