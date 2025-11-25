package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class PerfilClienteDto(
    val id_usuario: Long,
    val nombre_usuario: String,
    val correo: String,
    val saldo: String,
    val num_compras: Int,
)

@Serializable
data class PerfilClienteResponseDto(
    val ok: Boolean,
    val perfil: PerfilClienteDto? = null,
    val error: String? = null,
)

@Serializable
data class ActualizarPerfilRequestDto(
    val nombre_usuario: String,
    val correo: String,
    val contrasena: String? = null,
)

@Serializable
data class ComprasClienteItemDto(
    val id: Long,
    val nombre: String,
    val autor: String,
    val precio: Double? = null,
    val imagen_1_id: Long? = null,
    val calificacion_promedio: Double? = null,
    val compras: Int,
)

@Serializable
data class ComprasClienteResponseDto(
    val ok: Boolean,
    val rows: List<ComprasClienteItemDto>,
    val page: Int,
    val total_pages: Int,
)


// ====== MODELOS DE DOMINIO ======

data class PerfilCliente(
    val idUsuario: Long,
    val nombreUsuario: String,
    val correo: String,
    val saldo: String,
    val numCompras: Int,
)

data class CompraPerfil(
    val id: Long,
    val nombre: String,
    val autor: String,
    val precio: Double?,
    val imagenId: Long?,
    val calificacionPromedio: Double?,
    val compras: Int,
)
