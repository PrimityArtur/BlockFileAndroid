package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.*
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class ProductDetailResult(
    val detail: ProductDetail,
    val comments: List<ProductComment>,
)

interface ProductDetailRepository {
    suspend fun getProductDetail(productId: Long): ProductDetailResult
}

class ProductDetailRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : ProductDetailRepository {

    override suspend fun getProductDetail(productId: Long): ProductDetailResult {
        try {
            val res = api.getProductDetail(productId)
            if (!res.ok) throw Exception("Respuesta no válida del servidor")

            val detail = res.producto.toDomain()
            val comments = res.comentarios.map { it.toDomain() }

            return ProductDetailResult(detail, comments)
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }
}


fun ProductDetailDto.toDomain() = ProductDetail(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    precio = precio,
    saldoCliente = saldo_cliente,
    compras = compras,
    calificacionPromedio = calificacion_promedio,
    autor = autor,
    version = version,
    categoria = categoria,
    fechaPublicacion = fecha_publicacion,
    imagenUrls = imagen_urls,
    mostrarAcciones = mostrar_acciones,
    urlTtl = url_ttl,
    urlDescargar = url_descargar,
)

fun CommentDto.toDomain() = ProductComment(
    cliente = cliente,
    calificacion = calificacion,
    fecha = fecha,
    descripcion = descripcion,
)
