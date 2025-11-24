package com.example.blockfile.core.domain.rankings

import com.example.blockfile.core.data.repository.*
import javax.inject.Inject

class GetProductosMasCompradosUseCase @Inject constructor(
    private val repository: RankingRepository,
) {
    suspend operator fun invoke(page: Int): ProductosMasCompradosPage =
        repository.getProductosMasComprados(page)
}

class GetMejoresCompradoresUseCase @Inject constructor(
    private val repository: RankingRepository,
) {
    suspend operator fun invoke(page: Int): MejoresCompradoresPage =
        repository.getMejoresCompradores(page)
}

class GetProductosMejorCalificadosUseCase @Inject constructor(
    private val repository: RankingRepository,
) {
    suspend operator fun invoke(page: Int): ProductosMejorCalificadosPage =
        repository.getProductosMejorCalificados(page)
}
