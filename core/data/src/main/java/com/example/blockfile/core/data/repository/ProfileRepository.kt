package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.*
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ComprasPerfilPage(
    val items: List<CompraPerfil>,
    val page: Int,
    val totalPages: Int,
)

interface ProfileRepository {
    suspend fun getProfile(): PerfilCliente
    suspend fun updateProfile(
        nombreUsuario: String,
        correo: String,
        contrasena: String?,
    ): PerfilCliente

    suspend fun getCompras(page: Int): ComprasPerfilPage
}

class ProfileRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : ProfileRepository {

    override suspend fun getProfile(): PerfilCliente {
        try {
            val res = api.getPerfilCliente()
            val dto = res.perfil

            if (!res.ok || dto == null) {
                throw Exception(res.error ?: "No se pudo obtener el perfil.")
            }

            return dto.toDomain()
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun updateProfile(
        nombreUsuario: String,
        correo: String,
        contrasena: String?,
    ): PerfilCliente {
        try {
            val body = ActualizarPerfilRequestDto(
                nombre_usuario = nombreUsuario,
                correo = correo,
                contrasena = contrasena?.takeIf { it.isNotBlank() },
            )
            val res = api.actualizarPerfilCliente(body)
            val dto = res.perfil  // ⬅ variable local

            if (!res.ok || dto == null) {
                throw Exception(res.error ?: "No se pudo actualizar el perfil.")
            }

            return dto.toDomain()
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun getCompras(page: Int): ComprasPerfilPage {
        try {
            val res = api.getComprasCliente(page)
            if (!res.ok) {
                throw Exception("No se pudieron obtener las compras.")
            }
            return ComprasPerfilPage(
                items = res.rows.map { it.toDomain() },
                page = res.page,
                totalPages = res.total_pages,
            )
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    private fun PerfilClienteDto.toDomain() = PerfilCliente(
        idUsuario = id_usuario,
        nombreUsuario = nombre_usuario,
        correo = correo,
        saldo = saldo,
        numCompras = num_compras,
    )

    private fun ComprasClienteItemDto.toDomain() = CompraPerfil(
        id = id,
        nombre = nombre,
        autor = autor,
        precio = precio,
        imagenId = imagen_1_id,
        calificacionPromedio = calificacion_promedio,
        compras = compras,
    )

    private fun parseErrorBody(e: HttpException): String {
        val rawBody = e.response()?.errorBody()?.string()
        return rawBody ?: "Error del servidor (${e.code()})."
    }
}
