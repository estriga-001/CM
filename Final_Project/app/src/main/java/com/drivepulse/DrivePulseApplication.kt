/**
 * Application class for DrivePulse.
 * Initializes Hilt dependency injection and Timber logging.
 *
 * Camada: App
 * Feature: Core
 */
package com.drivepulse

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Entry point for the DrivePulse application.
 * Annotated with @HiltAndroidApp to trigger Hilt code generation.
 */
@HiltAndroidApp
class DrivePulseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Initialize Timber for debug logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
