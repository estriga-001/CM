/**
 * Implementação concreta do [LocationTracker] usando FusedLocationProviderClient.
 *
 * Camada: Core / Location
 * Feature: Run
 *
 * Usa callbackFlow para converter a API de callback do FusedLocationProvider
 * num Flow reativo compatível com coroutines. Intervalo de 3 segundos, alta precisão.
 */
package com.drivepulse.core.location

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rastreio de localização GPS usando a API FusedLocationProvider do Google Play Services.
 *
 * @param fusedLocationClient cliente injetado pelo Hilt (via [LocationModule]).
 */
@Singleton
class FusedLocationTracker @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationTracker {

    /**
     * Inicia as atualizações de localização e expõe-as como um [Flow<Location>].
     *
     * - Intervalo normal: 3 segundos (adequado para tracking de condução).
     * - Intervalo mínimo: 1 segundo (para resposta rápida em curvas).
     * - Prioridade: PRIORITY_HIGH_ACCURACY (GPS + WiFi + rede).
     *
     * O Flow é cancelado automaticamente quando o coletor sai do scope
     * (ex: quando o Foreground Service é destruído), garantindo que
     * as atualizações de localização são sempre removidas.
     *
     * @throws SecurityException se as permissões de localização não estiverem concedidas.
     *   Deve ser verificado antes de chamar este método.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL_MS
        ).apply {
            setMinUpdateIntervalMillis(LOCATION_MIN_UPDATE_INTERVAL_MS)
            setWaitForAccurateLocation(false) // Não esperar por GPS — melhor UX
        }.build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    Timber.d("📍 Nova localização: lat=${location.latitude}, lon=${location.longitude}")
                    trySend(location) // Emite para o Flow sem bloquear
                }
            }
        }

        Timber.d("🟢 FusedLocationTracker: a iniciar atualizações")
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        // awaitClose garante que as atualizações são canceladas quando o Flow termina
        awaitClose {
            Timber.d("🔴 FusedLocationTracker: a parar atualizações")
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    companion object {
        /** Intervalo preferido entre atualizações GPS (3 segundos). */
        private const val LOCATION_UPDATE_INTERVAL_MS = 3_000L

        /** Intervalo mínimo aceitável entre atualizações GPS (1 segundo). */
        private const val LOCATION_MIN_UPDATE_INTERVAL_MS = 1_000L
    }
}
