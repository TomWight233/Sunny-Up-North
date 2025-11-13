package com.example.sunnyupnorth

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sunnyupnorth.ui.theme.SunnyUpNorthTheme
import com.squareup.moshi.Json
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

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


fun formatTime(timestamp: Long, timezoneOffset: Long):String {
    val date = Date((timestamp + timezoneOffset) * 1000)
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(date)
}

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
    val weather: List<WeatherDescription>,
    val sys: Sys,
    val timezone: Long
)

data class MainInfo(val temp: Double, val humidity: Int, @Json(name = "feels_like") val feelsLike: Double,
                    @Json(name = "temp_min") val tempMin: Double,
                    @Json(name = "temp_max") val tempMax: Double)
data class WeatherDescription(val main: String, val description: String, val icon: String)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)

@Composable
fun WeatherScreen(viewModel: WeatherViewModel, onRestart: () -> Unit) {
    val weather = viewModel.weather
    val skyGradient = Brush.verticalGradient(
        colors = listOf(lightSkyBlue, deepSkyBlue)
    )

    LaunchedEffect(Unit) {
        if (viewModel.searchLocation.isNotBlank()) {
            viewModel.fetchWeather(viewModel.searchLocation)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(brush = skyGradient).padding(top = 50.dp)

    ) {
        if (weather != null) {
            Text("${weather.name}", style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White

            ))

            val imageModifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color.Black, CircleShape)

            when (weather.weather[0].main.lowercase()){
            "clouds" -> Image(
                painter = painterResource(id = R.drawable.cloud),
                contentDescription = "Clouds Icon",
                contentScale = ContentScale.Crop
            )
                "clear" -> Image(
                    painter = painterResource(id = R.drawable.clear),
                    contentDescription = "Sunny Clear Icon",
                    contentScale = ContentScale.Crop

                )
                "rain" -> Image(
                    painter = painterResource(id = R.drawable.rain),
                    contentDescription = "Rain Icon",
                    contentScale = ContentScale.Crop

                )
                else -> Image(
                    painter = painterResource(id = R.drawable.unknown),
                    contentDescription = "Unknown image",
                    contentScale = ContentScale.Crop

                )

            }
            Spacer(Modifier.height(16.dp))
            Text("${weather.main.temp.toInt()}°C" , fontWeight = FontWeight.Bold, fontSize = 64.sp, textAlign = TextAlign.Center, color = Color.White)
            Text("Min:${weather.main.tempMin.toInt()}°C - Max: ${weather.main.tempMax.toInt()}°C", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Condition: ${weather.weather[0].description}", color = Color.White)
            Spacer(Modifier.height(25.dp))

            Column( modifier = Modifier.fillMaxWidth().padding(start = 110.dp), horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.feelslike),
                    contentDescription = "Feelslike Icon",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Feels Like: ${weather.main.feelsLike.toInt()}°C", fontSize = 16.sp, color = Color.White
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.humidity),
                    contentDescription = "Humidity Icon",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Humidity: ${(weather.main.humidity)}%",
                    color = Color.White
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.sunrise),
                    contentDescription = "Sunrise Icon",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Sunrise: ${formatTime(weather.sys.sunrise, weather.timezone)}",
                    color = Color.White
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.sunset),
                    contentDescription = "Sunset Icon",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Sunset: ${formatTime(weather.sys.sunset, weather.timezone)}",
                    color = Color.White
                )
            }
            }
        } else {
            Text("Enter a city to get the weather.")
        }
        Spacer(Modifier.height(50.dp))
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