package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.CatalogItemDto
import com.example.blockfile.core.model.CatalogResponseDto
import com.example.blockfile.core.model.ProductoCatalogo
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class CatalogPage(
    val items: List<ProductoCatalogo>,
    val page: Int,
    val totalPages: Int,
)

interface CatalogRepository {
    suspend fun getCatalogPage(
        page: Int,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): CatalogPage
}

class CatalogRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : CatalogRepository {

    override suspend fun getCatalogPage(
        page: Int,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): CatalogPage {
        try {
            val res: CatalogResponseDto = api.getCatalog(
                page = page,
                nombre = nombre?.takeIf { it.isNotBlank() },
                autor = autor?.takeIf { it.isNotBlank() },
                categoria = categoria?.takeIf { it.isNotBlank() },
            )

            val productos = res.rows.map { it.toDomain() }

            return CatalogPage(
                items = productos,
                page = res.page,
                totalPages = res.total_pages,
            )
        } catch (e: HttpException) {
            throw Exception("Error en servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }
}

private fun CatalogItemDto.toDomain(): ProductoCatalogo =
    ProductoCatalogo(
        id = id,
        nombre = nombre,
        autor = autor,
        precio = precio,
        imagenId = imagen_1_id,
        calificacionPromedio = calificacion_promedio,
        compras = compras,
    )
