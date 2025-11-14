package com.example.sunnyupnorth

class FakeHomeScreenViewModel : WeatherViewModel() {
    init {
        savedLocations[0] = ("London")
        savedLocations[1] = ("Sheffield")
        savedLocations[2] = ("Los Angeles")
//        savedLocations[3] = ("Dudley")
//        savedLocations[4] = ("Hogwarts")
//        savedLocations[5] = ("Paris")
//        savedLocations[6] = ("Edinburgh")
//        savedLocations[7] = ("A cabin in the woods")
    }
}