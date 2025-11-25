package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.AdminProductItem
import com.example.blockfile.core.model.AdminProductItemDto
import javax.inject.Inject

data class AdminProductsPage(
    val items: List<AdminProductItem>,
    val page: Int,
    val totalPages: Int,
)

interface AdminProductsRepository {
    suspend fun getAdminProductsPage(
        page: Int,
        id: Long?,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): AdminProductsPage
}

class AdminProductsRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : AdminProductsRepository {

    override suspend fun getAdminProductsPage(
        page: Int,
        id: Long?,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): AdminProductsPage {
        val response = api.getAdminProducts(
            page = page,
            id = id,
            nombre = nombre?.takeIf { it.isNotBlank() },
            autor = autor?.takeIf { it.isNotBlank() },
            categoria = categoria?.takeIf { it.isNotBlank() },
        )

        val items = response.rows.map { it.toDomain() }

        return AdminProductsPage(
            items = items,
            page = response.page,
            totalPages = response.total_pages,
        )
    }
}

private fun AdminProductItemDto.toDomain(): AdminProductItem =
    AdminProductItem(
        id = id,
        nombre = nombre,
        autor = autor,
        categoria = categoria,
        promedio = promedio,
    )
