package com.example.weatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherData("Danang")
        SearchCity()
    }

    private fun SearchCity() {
        val city = binding.searchView
        city.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query.isNullOrBlank() || query=="*") {
                    fetchWeatherByLocation()
                } else {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName : String) {
        val BASE_URL: String = "https://api.openweathermap.org/data/2.5/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)


        lifecycleScope.launch {
            try {
                val response = retrofit.getWeatherData(
                    cityName,
                    "eb0157648b9ed12cd1635d6bc2534568",
                    "metric",
                    "vi"
                )

//                query data
                    val temperature = response.main.temp.toString()
//                    Log.d("TAG" ,"onResponse : $temperature ")
                    val humidity = response.main.humidity
                    val windSpeed = response.wind.speed
                    val sunRise = response.sys.sunrise
                    val sunSet = response.sys.sunset
                    val seaLevel = response.main.pressure
                    val condition = response.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = response.main.temp_max
                    val minTemp = response.main.temp_min
                    val Name = response.name
//                    add data
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text= "${time(sunRise.toLong())}"
                    binding.sunset.text="${time(sunSet.toLong())}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.conditions.text = condition
                    binding.day.text = dayName(response.dt.toLong())
                    binding.date.text = date()
                    binding.cityName.text = "$Name"
//                    change icon
                    changeImagesAccordingToWeatherConditon(condition)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi khi gọi API", e)
            }
        }
    }


    private fun fetchWeatherByLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        val location: Location? = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            val lat = location.latitude
            val lon = location.longitude
            Log.d("LOCATION_COORDS", "Latitude: $lat, Longitude: $lon")
            fetchWeatherDataByCoordinates(lat, lon)
        } else {
            Toast.makeText(this, "Không lấy được vị trí", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchWeatherDataByCoordinates(lat: Double, lon: Double) {
        val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiInterface::class.java)

        lifecycleScope.launch {
            try {
                val response = retrofit.getWeatherByCoordinates(
                    lat, lon,
                    "eb0157648b9ed12cd1635d6bc2534568",
                    "metric",
                    "vi"
                )

                val temperature = response.main.temp.toString()
                val humidity = response.main.humidity
                val windSpeed = response.wind.speed
                val sunRise = response.sys.sunrise
                val sunSet = response.sys.sunset
                val seaLevel = response.main.pressure
                val condition = response.weather.firstOrNull()?.main ?: "unknown"
                val maxTemp = response.main.temp_max
                val minTemp = response.main.temp_min
                val Name = response.name

                binding.temp.text = "$temperature °C"
                binding.weather.text = condition
                binding.maxTemp.text = "Max Temp: $maxTemp °C"
                binding.minTemp.text = "Min Temp: $minTemp °C"
                binding.humidity.text = "$humidity %"
                binding.windSpeed.text = "$windSpeed m/s"
                binding.sunrise.text = time(sunRise.toLong())
                binding.sunset.text = time(sunSet.toLong())
                binding.sea.text = "$seaLevel hPa"
                binding.conditions.text = condition
                binding.day.text = dayName(response.dt.toLong())
                binding.date.text = date()
                binding.cityName.text = Name

                changeImagesAccordingToWeatherConditon(condition)

            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi khi gọi API theo tọa độ", e)
            }
        }
    }

    private fun changeImagesAccordingToWeatherConditon(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView2.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" , "Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView2.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" , "Snow" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView2.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView2.setAnimation(R.raw.sun)
            }

        }
        binding.lottieAnimationView2.playAnimation()

    }

}
private fun date(): String {
    val sdf = SimpleDateFormat("dd. MMMM yyyy", Locale.getDefault())
    return sdf.format(Date())
}

private fun time(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH : mm", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000))
}

fun dayName(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    return sdf.format(Date(timestamp * 1000)) // Nhân 1000 nếu timestamp là dạng UNIX time (giây)
}

