/**
 * Implementação remota via Firebase Firestore.
 *
 * Camada: Data (Remote)
 * Feature: Community / Sync
 */
package com.drivepulse.data.remote.datasource

import com.drivepulse.data.remote.model.RunDto
import com.drivepulse.data.remote.model.toDto
import com.drivepulse.domain.model.Run
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RunRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RunRemoteDataSource {

    companion object {
        private const val COLLECTION_RUNS = "runs"
        private const val FIELD_START_TIME = "startTime"
    }

    override suspend fun publishRun(run: Run, userName: String) {
        val dto = run.toDto(userName = userName)
        // Faz upsert usando o mesmo ID do Room
        firestore.collection(COLLECTION_RUNS)
            .document(dto.id)
            .set(dto)
            .await()
    }

    override fun getCommunityRuns(): Flow<List<Run>> = callbackFlow {
        // Escuta atualizações da coleção, ordenadas pela mais recente primeiro.
        // Limitamos a 50 para poupar leituras, no futuro deveríamos implementar paginação.
        val listenerRegistration = firestore.collection(COLLECTION_RUNS)
            .orderBy(FIELD_START_TIME, Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Podemos logar ou fechar o flow com erro
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val runs = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(RunDto::class.java)?.toDomain()
                        } catch (e: Exception) {
                            null // Ignorar documentos mal formados
                        }
                    }
                    trySend(runs)
                }
            }

        // Remove o listener quando o flow é cancelado (e.g. o ViewModel limpa a coroutine)
        awaitClose { listenerRegistration.remove() }
    }
}
