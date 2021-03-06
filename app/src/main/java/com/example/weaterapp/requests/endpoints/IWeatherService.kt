package com.example.weaterapp.requests.endpoints

import com.example.weaterapp.requests.entity_requests.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherService {
    @GET("find?")
    fun find(@Query("q") cityName: String,
              @Query("appid") appId: String, @Query("lang") lang: String,
             @Query("units") unit: String): Call<FindResult>

    @GET("group?")
    fun findGroup(@Query("id") id: String, @Query("appid") appId: String,
                  @Query("lang") lang: String,
                  @Query("units") unit: String): Call<FindResult>
}
