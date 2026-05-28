package com.drivepulse.core.common

import com.drivepulse.R

/**
 * MVP generator for stylized car avatars based on user selection.
 * In the future, this can call a Cloud Function to generate AI images.
 * Currently returns a local drawable resource ID simulating the chosen car.
 */
object CarAvatarGenerator {

    /**
     * Generates a local drawable resource for the chosen car specs.
     * 
     * @param brand the chosen car brand.
     * @param model the chosen car model.
     * @param year the chosen car year.
     * @return a drawable resource ID representing the car.
     */
    fun generateLocalAvatar(brand: String, model: String, year: Int): Int {
        // MVP: Provide generic vector silhouettes depending on keywords.
        // In a real app, this would map to dozens of custom local SVGs.
        val lowerModel = model.lowercase()
        return when {
            lowerModel.contains("suv") -> R.drawable.ic_launcher_background // TODO: Replace with generic SUV icon
            lowerModel.contains("sport") -> R.drawable.ic_launcher_background // TODO: Replace with generic sport icon
            else -> R.drawable.ic_launcher_background // TODO: Replace with generic sedan icon
        }
    }
}
