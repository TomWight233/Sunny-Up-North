package com.example.sunnyupnorth


class FakeWeatherViewModel : WeatherViewModel() {
    init {
        weather = WeatherResponse(
            name = "London",
            main = MainInfo(temp = 15.5, humidity = 80),
            weather = listOf(WeatherDescription(description = "Light rain", icon = "10d"))
        )
    }
}
