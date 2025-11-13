package com.example.sunnyupnorth

class FakeWeatherViewModel : WeatherViewModel() {
    init {
        weather = WeatherResponse(
            name = "London",
            main = MainInfo(
                temp = 15.5,
                feelsLike = 14.8,
                tempMin = 12.0,
                tempMax = 18.0,
                humidity = 80
            ),
            weather = listOf(
                WeatherDescription(
                    main = "Clouds",
                    description = "Scattered clouds",
                    icon = "03d"
                )
            ),
            sys = Sys(
                sunrise = 1731484800, // Example timestamp (UNIX, seconds)
                sunset = 1731520800   // Example timestamp (UNIX, seconds)
            )
        )
    }
}
