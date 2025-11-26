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


data class AdminCategoryItem(
    val id: Long,
    val nombre: String,
    val descripcion: String,
)


@Serializable
data class SaveCategoryRequestDto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String = "",
)

@Serializable
data class SaveCategoryResponseDto(
    val ok: Boolean,
    val id: Long,
    val message: String? = null,
)

@Serializable
data class DeleteCategoryRequestDto(
    val id_categoria: Long,
)