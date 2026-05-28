/**
 * Módulo Hilt para fornecer o FusedLocationProviderClient e o LocationTracker.
 *
 * Camada: Data / DI
 * Feature: Location
 *
 * Separado do DataModule para manter responsabilidades isoladas.
 * O FusedLocationProviderClient precisa do ApplicationContext.
 */
package com.drivepulse.data.di

import android.content.Context
import com.drivepulse.core.location.FusedLocationTracker
import com.drivepulse.core.location.LocationTracker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para dependências de localização.
 *
 * Fornece o [FusedLocationProviderClient] e a implementação de [LocationTracker].
 * Instalado no [SingletonComponent] para viver enquanto a aplicação existir,
 * garantindo que o Foreground Service pode aceder ao tracker sem re-instanciação.
 */
@Module
@InstallIn(SingletonComponent::class)
object LocationModule {

    /**
     * Fornece o [FusedLocationProviderClient] usando o ApplicationContext.
     * Singleton para evitar criar múltiplos clientes de localização.
     */
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    /**
     * Fornece a implementação de [LocationTracker] como [FusedLocationTracker].
     * Ao injetar a interface, os ViewModels e Services ficam desacoplados da implementação.
     */
    @Provides
    @Singleton
    fun provideLocationTracker(
        fusedLocationTracker: FusedLocationTracker
    ): LocationTracker {
        return fusedLocationTracker
    }
}
