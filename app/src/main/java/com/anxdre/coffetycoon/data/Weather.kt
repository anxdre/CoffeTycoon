package com.anxdre.coffetycoon.data

import android.content.Context
import com.anxdre.coffetycoon.R
import java.io.Serializable
import kotlin.random.Random

data class Weather(val name: String, val bgIcon: Int, val threshold: Long) : Serializable

fun getListOfWeather(context: Context): ArrayList<Weather> {
    val listOfWeather = arrayListOf<Weather>()
    listOfWeather.add(
        Weather(
            context.resources.getStringArray(R.array.weather)[0],
            R.drawable.ic_weather_sunny,
            5000
        )
    )
    listOfWeather.add(
        Weather(
            context.resources.getStringArray(R.array.weather)[1],
            R.drawable.ic_weather_cloudy,
            5800
        )
    )
    listOfWeather.add(
        Weather(
            context.resources.getStringArray(R.array.weather)[2],
            R.drawable.ic_weather_rainy,
            10000
        )
    )
    listOfWeather.add(
        Weather(
            context.resources.getStringArray(R.array.weather)[3],
            R.drawable.ic_weather_storm,
            20000
        )
    )
    return listOfWeather
}

fun randomizeWeather(context: Context, listOfWeather: ArrayList<Weather>): Weather {
    listOfWeather.shuffle(Random(Random.nextLong()))
    return listOfWeather.random(Random(Random.nextInt()))
}