package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AdminUserListItemDto(
    val id: Long,
    val nombre: String,
    val saldo: Double? = null,   // Decimal recibido como string
)

@Serializable
data class AdminUsersResponseDto(
    val ok: Boolean,
    val rows: List<AdminUserListItemDto>,
    val page: Int,
    val total_pages: Int,
)

@Serializable
data class AdminUserDetailDto(
    val ok: Boolean,
    val id: Long,
    val nombre: String,
    val correo: String? = null,
    val fecha: String? = null,
    val saldo: String? = null,
)

/* Dominio para UI */

data class AdminUserItem(
    val id: Long,
    val nombre: String,
    val saldo: String,   // Lo mostramos como texto
)

data class AdminUserDetail(
    val id: Long,
    val nombre: String,
    val correo: String?,
    val fecha: String?,
    val saldo: String?,
)

/* CRUD DTOs */

@Serializable
data class SaveUserSaldoRequestDto(
    val id: Long,
    val saldo: String,
)

@Serializable
data class SaveUserSaldoResponseDto(
    val ok: Boolean,
    val id: Long,
    val message: String? = null,
)

@Serializable
data class DeleteUserRequestDto(
    val id_usuario: Long,
)
