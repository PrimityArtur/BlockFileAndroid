package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.AdminCategoriesResponseDto
import com.example.blockfile.core.model.AdminCategoryItem
import com.example.blockfile.core.model.AdminCategoryItemDto
import javax.inject.Inject

data class AdminCategoriesPage(
    val items: List<AdminCategoryItem>,
    val page: Int,
    val totalPages: Int,
)

class AdminCategoriesRepository @Inject constructor(
    private val api: BlockFileApi,
) {

    suspend fun getAdminCategoriesPage(
        page: Int,
        id: Long?,
        nombre: String?,
        descripcion: String?,
    ): AdminCategoriesPage {
        val resp: AdminCategoriesResponseDto = api.getAdminCategoriesPage(
            page = page,
            id = id?.toString(),
            nombre = nombre?.takeIf { it.isNotBlank() },
            descripcion = descripcion?.takeIf { it.isNotBlank() },
        )

        if (!resp.ok) {
            throw IllegalStateException("La API devolvió ok=false al listar categorías")
        }

        val items = resp.rows.map { it.toDomain() }

        return AdminCategoriesPage(
            items = items,
            page = resp.page,
            totalPages = resp.total_pages,
        )
    }
}

private fun AdminCategoryItemDto.toDomain() = AdminCategoryItem(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
)
