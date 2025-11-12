package com.example.sunnyupnorth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import com.example.sunnyupnorth.ui.theme.SunnyUpNorthTheme
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunnyUpNorthTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App()

                }
            }
        }
    }
}

@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = viewModel()


    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onStartWeather = {
                    navController.navigate("weather") }
            )
        }

//// --- WEATHER SCREEN ---
//        composable("weather") {
//            WeatherScreen(
//                viewModel = viewModel,
//                onNext = {
//                    val hasNext = viewModel.nextQuestion()
//                    if (!hasNext) {
//                        navController.navigate("results")
//                    }
//                }
//            )
//        }



    }
}

val lightSkyBlue = Color(0xFF81D4FA) // A light, clear sky blue
val deepSkyBlue = Color(0xFF0288D1)  // A deeper, vibrant blue

@Composable
fun HomeScreen(
    onStartWeather: () -> Unit) {
    val viewModel = WeatherViewModel()
    val skyGradient = Brush.verticalGradient(
        colors = listOf(lightSkyBlue, deepSkyBlue)
    )
    Column(
        modifier = Modifier
            .background(brush = skyGradient)
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.iasun),
            contentDescription = "Sunny Up North Logo",
            modifier = Modifier.size(360.dp)

        )
        OutlinedTextField(
            value = viewModel.searchLocation,
            onValueChange = { viewModel.onSearchChange(it) },
            label = { Text("Search Location") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                IconButton(onClick = onStartWeather) { // <-- The change is here
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                }
            }
        )
    }
}



interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}


data class WeatherResponse(
    val name: String,
    val main: MainInfo,
    val weather: List<WeatherDescription>
)

data class MainInfo(val temp: Double, val humidity: Int)
data class WeatherDescription(val description: String, val icon: String)


@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weather = viewModel.weather

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (weather != null) {
            Text("City: ${weather.name}")
            Text("Temp: ${weather.main.temp}°C")
            Text("Condition: ${weather.weather[0].description}")
        } else {
            Text("Enter a city to get the weather.")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherPreview() {
    SunnyUpNorthTheme {
        HomeScreen(onStartWeather = {})
    }
}