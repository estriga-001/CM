package com.example.section3_android.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.section3_android.model.ImageItem
import com.example.section3_android.repository.ImageRepository

/**
 * ViewModel for MainActivity.
 * Fetches dog images from the repository and exposes them via LiveData.
 */
class MainViewModel : ViewModel() {

    private val repository: ImageRepository = ImageRepository()

    // List of dog image items fetched so far
    private val _imageList: MutableLiveData<List<ImageItem>> = MutableLiveData<List<ImageItem>>()
    val imageList: LiveData<List<ImageItem>> get() = _imageList

    // Loading state
    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Error messages
    private val _errorMessage: MutableLiveData<String> = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Internal mutable list to accumulate images
    private val currentImages: ArrayList<ImageItem> = ArrayList()

    init {
        _imageList.value = ArrayList()
    }

    /**
     * Fetches a batch of random dog images (one at a time, repeated 'count' times).
     * Each successful response adds an image to the list.
     */
    fun fetchDogImages(count: Int) {
        _isLoading.value = true

        // Track how many calls have completed
        var completedCount = 0

        for (i in 0 until count) {
            repository.fetchRandomDogImage(object : ImageRepository.ImageRepositoryCallback {
                override fun onSuccess(imageItem: ImageItem) {
                    currentImages.add(imageItem)
                    completedCount++

                    if (completedCount >= count) {
                        _imageList.postValue(ArrayList(currentImages))
                        _isLoading.postValue(false)
                    }
                }

                override fun onError(errorMessage: String) {
                    completedCount++
                    _errorMessage.postValue(errorMessage)

                    if (completedCount >= count) {
                        _imageList.postValue(ArrayList(currentImages))
                        _isLoading.postValue(false)
                    }
                }
            })
        }
    }

    /**
     * Clears the current image list and fetches a fresh batch.
     */
    fun refreshImages(count: Int) {
        currentImages.clear()
        _imageList.value = ArrayList()
        fetchDogImages(count)
    }
}
