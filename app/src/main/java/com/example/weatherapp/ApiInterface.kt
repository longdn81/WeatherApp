package com.example.weatherapp

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
interface ApiInterface {
    @GET("weather")
    suspend fun getWeatherData(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): WeatherApp
}