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
    val ok: Boolean? = null,
    val rows: List<AdminProductItemDto>,
    val page: Int,
    val total_pages: Int,
)

@Serializable
data class AdminProductDetailDto(
    val id: Long,
    val nombre: String,
    val fecha: String? = null,
    val calificacion: Double? = null,
    val descripcion: String = "",
    val version: String = "",
    val precio: String = "0.00",
    val categoria_id: Long? = null,
    val categoria: String? = null,
    val autor_id: Long? = null,
    val autor: String? = null,
    val imagenes: List<AdminProductImageDto> = emptyList(),
    val activo: Boolean = true,
    val tiene_archivo: Boolean = false,
)

@Serializable
data class SaveAdminProductRequestDto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String,
    val version: String,
    val precio: String,
    val id_autor: Long? = null,
    val id_categoria: Long? = null,
    val activo: Boolean = true,
)

@Serializable
data class SaveAdminProductResponseDto(
    val ok: Boolean? = null,
    val id: Long,
)

data class AdminProductItem(
    val id: Long,
    val nombre: String,
    val autor: String,
    val categoria: String,
    val promedio: Double?,
)


data class AdminProductDetail(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val version: String,
    val precio: String,
    val autorId: Long?,
    val categoriaId: Long?,
    val activo: Boolean,
    val tieneArchivo: Boolean,
    val imagenes: List<AdminProductImage>,
)

@Serializable
data class AdminProductImageDto(
    val id: Long,
    val orden: Int,
    val url: String,
)

data class AdminProductImage(
    val id: Long,
    val orden: Int,
    val url: String,
)
