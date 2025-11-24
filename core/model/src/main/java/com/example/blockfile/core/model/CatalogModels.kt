package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CatalogItemDto(
    val id: Long,
    val nombre: String,
    val autor: String,
    val precio: Double,
    val imagen_1_id: Long? = null,
    val calificacion_promedio: Double? = null,
    val compras: Int = 0,
)

@Serializable
data class CatalogResponseDto(
    val ok: Boolean,
    val rows: List<CatalogItemDto>,
    val page: Int,
    val total_pages: Int,
)

data class ProductoCatalogo(
    val id: Long,
    val nombre: String,
    val autor: String,
    val precio: Double,
    val imagenId: Long?,
    val calificacionPromedio: Double?,
    val compras: Int,
)