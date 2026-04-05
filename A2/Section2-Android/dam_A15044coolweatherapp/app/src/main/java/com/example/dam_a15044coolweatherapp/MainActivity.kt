package com.example.dam_a15044coolweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import com.example.dam_a15044coolweatherapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Use DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.container) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Observe weather code and image name to update the icon manually
        viewModel.weatherImageName.observe(this) { imageName ->
            updateWeatherIcon(imageName, viewModel.weatherCode.value ?: 0)
        }

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                // Re-init location in ViewModel if needed or it will auto-retry
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    private fun updateWeatherIcon(imageName: String, code: Int) {
        val isNightTheme = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES
        val day = !isNightTheme

        val res = resources
        val packageName = packageName
        
        // Try to find day/night specific version
        val suffix = if (day) "_day" else "_night"
        var resID = res.getIdentifier(imageName + suffix, "drawable", packageName)
        
        if (resID == 0) {
            resID = res.getIdentifier(imageName, "drawable", packageName)
        }

        if (resID != 0) {
            binding.weatherImage.setImageResource(resID)
        }

        // Apply tint based on weather (optional logic)
        val tintColor = when (code) {
            0 -> if (day) R.color.day_primary else R.color.night_primary
            1, 2, 3 -> if (day) R.color.day_text_secondary else R.color.night_text_secondary
            45, 48 -> if (day) R.color.day_text_primary else R.color.night_text_primary
            else -> if (day) R.color.day_primary else R.color.night_primary
        }

        ImageViewCompat.setImageTintList(
            binding.weatherImage,
            ColorStateList.valueOf(ContextCompat.getColor(this, tintColor))
        )
    }
}