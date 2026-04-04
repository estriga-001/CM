package com.example.section3_android.repository

import com.example.section3_android.api.DogApiService
import com.example.section3_android.api.RetrofitClient
import com.example.section3_android.model.ImageItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository class that handles data fetching from the Dog API.
 * Acts as a single source of truth between the API and the ViewModel.
 */
class ImageRepository {

    private val apiService: DogApiService

    init {
        apiService = RetrofitClient.getClient().create(DogApiService::class.java)
    }

    /**
     * Fetches a random dog image from the API.
     * Uses a callback interface to return the result to the caller.
     */
    fun fetchRandomDogImage(callback: ImageRepositoryCallback) {
        val call: Call<ImageItem> = apiService.getRandomDogImage()

        call.enqueue(object : Callback<ImageItem> {
            override fun onResponse(call: Call<ImageItem>, response: Response<ImageItem>) {
                if (response.isSuccessful && response.body() != null) {
                    callback.onSuccess(response.body()!!)
                } else {
                    callback.onError("Response was not successful")
                }
            }

            override fun onFailure(call: Call<ImageItem>, t: Throwable) {
                callback.onError(t.message ?: "Unknown error occurred")
            }
        })
    }

    /**
     * Callback interface for repository results.
     */
    interface ImageRepositoryCallback {
        fun onSuccess(imageItem: ImageItem)
        fun onError(errorMessage: String)
    }
}
