package com.example.weaterapp.requests.entity_requests

import androidx.room.Entity
import androidx.room.PrimaryKey

data class FindResult(val list: List<City>)

data class Main (
    val temp: Float,
    val pressure: Int
)

data class City(
    val id: Int,
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val sys: Sys,
    val wind: Wind,
    val clouds: Cloud
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val country: String
)

data class Wind(
    val speed: Float
)

data class Cloud(
    val all: Long
)

@Entity(tableName = "TB_FAVORITE")
data class Favorite(
    @PrimaryKey
    val id: Int,
    val name: String
)
