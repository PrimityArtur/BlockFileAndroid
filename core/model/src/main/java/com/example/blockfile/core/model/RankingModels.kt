package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

// ========== DTOs DE RESPUESTA BACKEND ==========

@Serializable
data class ProductoMasCompradoDto(
    val id: Long,
    val top: Int,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val precio: Double? = null,
    val compras: Int,
)

@Serializable
data class RankingProductosMasCompradosResponseDto(
    val ok: Boolean,
    val rows: List<ProductoMasCompradoDto>,
    val page: Int,
    val total_pages: Int,
)

@Serializable
data class MejorCompradorDto(
    val id_usuario: Long,
    val top: Int,
    val nombre: String,
    val compras: Int,
)

@Serializable
data class RankingMejoresCompradoresResponseDto(
    val ok: Boolean,
    val rows: List<MejorCompradorDto>,
    val page: Int,
    val total_pages: Int,
)

@Serializable
data class ProductoMejorCalificadoDto(
    val id: Long,
    val top: Int,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val precio: Double? = null,
    val n_calificaciones: Int,
    val calif_prom: Double,
)

@Serializable
data class RankingProductosMejorCalificadosResponseDto(
    val ok: Boolean,
    val rows: List<ProductoMejorCalificadoDto>,
    val page: Int,
    val total_pages: Int,
)

// ========== MODELOS DE DOMINIO PARA LA UI ==========

data class ProductoMasComprado(
    val id: Long,
    val top: Int,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val precio: Double?,
    val compras: Int,
)

data class MejorComprador(
    val idUsuario: Long,
    val top: Int,
    val nombre: String,
    val compras: Int,
)

data class ProductoMejorCalificado(
    val id: Long,
    val top: Int,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val precio: Double?,
    val numCalificaciones: Int,
    val promedio: Double,
)
