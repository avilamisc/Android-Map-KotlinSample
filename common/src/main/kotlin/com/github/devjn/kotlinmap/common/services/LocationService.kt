package com.github.devjn.kotlinmap.common.services

import com.github.devjn.kotlinmap.common.PlaceClusterItem
import com.github.devjn.kotlinmap.common.services.ServerRespose
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
* Created by @author Jakhongir on 27.9.2016
* devjn@jn-arts.com
* LocationService.kt
*/

interface LocationService {
    @GET("places")
    fun nearLocations(
            @Query("lat") lat: Double,
            @Query("lng") lng: Double): Call<Collection<PlaceClusterItem>>

    @get:GET("places_all")
    val all: Call<ServerRespose.MapAll>

    @get:GET("places_version")
    val version: Call<Int>

    companion object {
        val retrofit = Retrofit.Builder()
                //            .baseUrl("http://localhost:1337/")
                .baseUrl("http://10.0.2.2:1337/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
}
