package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.*
import javax.inject.Inject

data class AdminUsersPage(
    val items: List<AdminUserItem>,
    val page: Int,
    val totalPages: Int,
)

class AdminUsersRepository @Inject constructor(
    private val api: BlockFileApi,
) {

    suspend fun getAdminUsersPage(
        page: Int,
        id: Long?,
        nombre: String?,
        saldo: String?,
    ): AdminUsersPage {
        val resp: AdminUsersResponseDto = api.getAdminUsersPage(
            page = page,
            id = id?.toString(),
            nombre = nombre?.takeIf { it.isNotBlank() },
            saldo = saldo?.takeIf { it.isNotBlank() },
        )

        if (!resp.ok) {
            throw IllegalStateException("La API devolvió ok=false al listar usuarios")
        }

        val items = resp.rows.map { it.toDomain() }

        return AdminUsersPage(
            items = items,
            page = resp.page,
            totalPages = resp.total_pages,
        )
    }

    suspend fun getUserDetail(id: Long): AdminUserDetail {
        val dto: AdminUserDetailDto = api.getAdminUserDetail(id)
        if (!dto.ok) {
            throw IllegalStateException("No se pudo obtener el detalle del usuario")
        }
        return dto.toDomain()
    }

    suspend fun saveUserSaldo(
        id: Long,
        saldo: String,
    ): Long {
        val body = SaveUserSaldoRequestDto(id = id, saldo = saldo)
        val resp = api.saveAdminUserSaldo(body)
        if (!resp.ok) {
            throw IllegalStateException(resp.message ?: "Error al guardar usuario")
        }
        return resp.id
    }

    suspend fun deleteUser(id: Long) {
        val resp = api.deleteAdminUser(DeleteUserRequestDto(id_usuario = id))
        if (!resp.ok) {
            throw IllegalStateException(resp.message ?: "Error al eliminar usuario")
        }
    }
}

private fun AdminUserListItemDto.toDomain() = AdminUserItem(
    id = id,
    nombre = nombre,
    saldo = saldo?.toString() ?: ""
)

private fun AdminUserDetailDto.toDomain() = AdminUserDetail(
    id = id,
    nombre = nombre,
    correo = correo,
    fecha = fecha,
    saldo = saldo,
)
