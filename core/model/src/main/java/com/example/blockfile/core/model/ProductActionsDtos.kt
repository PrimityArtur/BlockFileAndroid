package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class CommentRequestDto(
    val descripcion: String,
)

@Serializable
data class SimpleResponseDto(
    val ok: Boolean,
    val message: String? = null,
    val calificacion_promedio: Double? = null,
)


@Serializable
data class RatingRequestDto(
    val calificacion: Int,
)


@Serializable
data class PurchaseResponseDto(
    val ok: Boolean,
    val message: String? = null,
    val saldo_cliente: Double? = null,
    val cliente_compro: Boolean? = null,
    val compras: Long? = null,
)