package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDetailResponseDto(
    val ok: Boolean,
    val producto: ProductDetailDto,
    val comentarios: List<CommentDto>,
)

@Serializable
data class ProductDetailDto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double? = null,
    val saldo_cliente: Double? = null,
    val compras: Int,
    val calificacion_promedio: Double,
    val autor: String,
    val version: String,
    val categoria: String,
    val fecha_publicacion: String? = null,      // ISO8601
    val imagen_urls: List<String> = emptyList(),
    val mostrar_acciones: Boolean = false,
    val url_ttl: String,
    val url_descargar: String,
)

@Serializable
data class CommentDto(
    val cliente: String,
    val calificacion: Int,
    val fecha: String? = null,
    val descripcion: String,
)

// ===== MODEL DE DOMINIO PARA LA UI =====

data class ProductDetail(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val precio: Double?,
    val saldoCliente: Double?,
    val compras: Int,
    val calificacionPromedio: Double,
    val autor: String,
    val version: String,
    val categoria: String,
    val fechaPublicacion: String?,
    val imagenUrls: List<String>,
    val mostrarAcciones: Boolean,
    val urlTtl: String,
    val urlDescargar: String,
)

data class ProductComment(
    val cliente: String,
    val calificacion: Int,
    val fecha: String?,
    val descripcion: String,
)
