package com.drivepulse.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drivepulse.core.common.CarAvatarGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete

    fun saveCarProfile(brand: String, model: String, year: Int) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            try {
                // Generates MVP avatar resource ID (saved as string for now)
                val avatarRes = CarAvatarGenerator.generateLocalAvatar(brand, model, year)
                
                val updates = mapOf(
                    "selectedCarBrand" to brand,
                    "selectedCarModel" to model,
                    "selectedCarYear" to year,
                    "generatedCarImageUrl" to "res:$avatarRes",
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("users").document(uid)
                    .update(updates).await()

                _isComplete.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                // Em caso de erro (ex: doc não existe), prossegue na mesma para não prender o utilizador
                _isComplete.value = true
            }
        }
    }
}
