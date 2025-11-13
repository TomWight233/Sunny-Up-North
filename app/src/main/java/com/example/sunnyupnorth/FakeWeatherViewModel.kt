package com.example.sunnyupnorth


class FakeWeatherViewModel : WeatherViewModel() {
    init {
        weather = WeatherResponse(
            name = "London",
            main = MainInfo(temp = 15.52, humidity = 80, feelsLike = 17.03),
            weather = listOf(WeatherDescription(main = "Clouds", description = "Light rain", icon = "10d"))
        )
    }
}
