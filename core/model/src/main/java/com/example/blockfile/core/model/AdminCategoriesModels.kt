package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminCategoryItemDto(
    val id: Long,
    val nombre: String,
    val descripcion: String = "",
)

@Serializable
data class AdminCategoriesResponseDto(
    val ok: Boolean,
    val rows: List<AdminCategoryItemDto>,
    val page: Int,
    val total_pages: Int,
)

/**
 * Modelo de dominio para usar en la UI
 */
data class AdminCategoryItem(
    val id: Long,
    val nombre: String,
    val descripcion: String,
)
