package com.example.section3_android.api

import com.example.section3_android.model.ImageItem
import retrofit2.Call
import retrofit2.http.GET

/**
 * Retrofit service interface for the Dog CEO API.
 * Base URL: https://dog.ceo/api/
 */
interface DogApiService {

    @GET("breeds/image/random")
    fun getRandomDogImage(): Call<ImageItem>
}
