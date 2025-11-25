package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsPage
import com.example.blockfile.core.data.repository.AdminProductsRepository
import javax.inject.Inject

class GetAdminProductsPageUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(
        page: Int,
        id: Long?,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): AdminProductsPage = repository.getAdminProductsPage(
        page = page,
        id = id,
        nombre = nombre,
        autor = autor,
        categoria = categoria,
    )
}
