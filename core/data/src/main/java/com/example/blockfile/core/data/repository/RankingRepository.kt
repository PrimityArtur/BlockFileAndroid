package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ProductosMasCompradosPage(
    val items: List<ProductoMasComprado>,
    val page: Int,
    val totalPages: Int,
)

data class MejoresCompradoresPage(
    val items: List<MejorComprador>,
    val page: Int,
    val totalPages: Int,
)

data class ProductosMejorCalificadosPage(
    val items: List<ProductoMejorCalificado>,
    val page: Int,
    val totalPages: Int,
)

interface RankingRepository {
    suspend fun getProductosMasComprados(page: Int): ProductosMasCompradosPage
    suspend fun getMejoresCompradores(page: Int): MejoresCompradoresPage
    suspend fun getProductosMejorCalificados(page: Int): ProductosMejorCalificadosPage
}

class RankingRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : RankingRepository {

    override suspend fun getProductosMasComprados(page: Int): ProductosMasCompradosPage {
        try {
            val res = api.getRankingProductosMasComprados(page)
            if (!res.ok) throw Exception("Respuesta no válida del servidor")

            return ProductosMasCompradosPage(
                items = res.rows.map { it.toDomain() },
                page = res.page,
                totalPages = res.total_pages,
            )
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun getMejoresCompradores(page: Int): MejoresCompradoresPage {
        try {
            val res = api.getRankingMejoresCompradores(page)
            if (!res.ok) throw Exception("Respuesta no válida del servidor")

            return MejoresCompradoresPage(
                items = res.rows.map { it.toDomain() },
                page = res.page,
                totalPages = res.total_pages,
            )
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun getProductosMejorCalificados(page: Int): ProductosMejorCalificadosPage {
        try {
            val res = api.getRankingProductosMejorCalificados(page)
            if (!res.ok) throw Exception("Respuesta no válida del servidor")

            return ProductosMejorCalificadosPage(
                items = res.rows.map { it.toDomain() },
                page = res.page,
                totalPages = res.total_pages,
            )
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }
}

// ====== Mappers DTO -> dominio ======

private fun ProductoMasCompradoDto.toDomain() = ProductoMasComprado(
    id = id,
    top = top,
    nombre = nombre,
    autor = autor,
    categoria = categoria,
    precio = precio,
    compras = compras,
)

private fun MejorCompradorDto.toDomain() = MejorComprador(
    idUsuario = id_usuario,
    top = top,
    nombre = nombre,
    compras = compras,
)

private fun ProductoMejorCalificadoDto.toDomain() = ProductoMejorCalificado(
    id = id,
    top = top,
    nombre = nombre,
    autor = autor,
    categoria = categoria,
    precio = precio,
    numCalificaciones = n_calificaciones,
    promedio = calif_prom,
)
