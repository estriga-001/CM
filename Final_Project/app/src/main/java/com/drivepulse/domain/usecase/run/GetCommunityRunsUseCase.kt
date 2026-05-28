/**
 * Use case para observar o feed de rotas da comunidade.
 *
 * Camada: Domain
 * Feature: Community
 */
package com.drivepulse.domain.usecase.run

import com.drivepulse.domain.model.Run
import com.drivepulse.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCommunityRunsUseCase @Inject constructor(
    private val runRepository: RunRepository
) {
    operator fun invoke(): Flow<List<Run>> {
        return runRepository.getCommunityRuns()
    }
}
