package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminProductItemDto(
    val id: Long,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val promedio: Double? = null,
)

@Serializable
data class AdminProductsResponseDto(
    val ok: Boolean,
    val rows: List<AdminProductItemDto>,
    val page: Int,
    val total_pages: Int,
)

/**
 * Modelo de dominio que usará la UI.
 */
data class AdminProductItem(
    val id: Long,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val promedio: Double?,
)
