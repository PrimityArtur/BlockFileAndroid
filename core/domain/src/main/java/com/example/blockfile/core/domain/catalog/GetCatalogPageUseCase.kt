package com.example.blockfile.core.domain.catalog

import com.example.blockfile.core.data.repository.CatalogPage
import com.example.blockfile.core.data.repository.CatalogRepository
import javax.inject.Inject

class GetCatalogPageUseCase @Inject constructor(
    private val repository: CatalogRepository,
) {
    suspend operator fun invoke(
        page: Int,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): CatalogPage = repository.getCatalogPage(page, nombre, autor, categoria)
}
