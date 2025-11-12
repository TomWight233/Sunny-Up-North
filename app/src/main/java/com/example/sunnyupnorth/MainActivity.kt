package com.example.sunnyupnorth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sunnyupnorth.ui.theme.SunnyUpNorthTheme
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunnyUpNorthTheme {
                App()

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
                viewModel = viewModel,
                onStartWeather = { city ->
                    viewModel.onSearchChange(city)
                    navController.navigate("weather")
                }
            )
        }

// --- WEATHER SCREEN ---
        composable("weather") {
            WeatherScreen(
                viewModel = viewModel,
                onRestart = {
                    navController.navigate("home")
                }
            )


        }
    }
}


val lightSkyBlue = Color(0xFF81D4FA) // A light, clear sky blue
val deepSkyBlue = Color(0xFF0288D1)  // A deeper, vibrant blue

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    onStartWeather: (String) -> Unit
) {
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
        TextField(
            value = viewModel.searchLocation,
            onValueChange = { viewModel.onSearchChange(it) },
            label = { Text("Search Location") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                IconButton(onClick = { onStartWeather(viewModel.searchLocation) }) { // <-- The change is here
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
fun WeatherScreen(viewModel: WeatherViewModel, onRestart: () -> Unit) {
    val weather = viewModel.weather

    LaunchedEffect(Unit) {
        if (viewModel.searchLocation.isNotBlank()) {
            viewModel.fetchWeather(viewModel.searchLocation)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (weather != null) {
            Text("${weather.name}", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(24.dp))
            Text("Temp: ${weather.main.temp}°C")
            Text("Condition: ${weather.weather[0].description}")
        } else {
            Text("Enter a city to get the weather.")
        }
        Button(onClick = onRestart) {
            Text("Return home")
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeatherPreview() {
    SunnyUpNorthTheme {
        WeatherScreen(viewModel = FakeWeatherViewModel(), onRestart = {})
    }
}