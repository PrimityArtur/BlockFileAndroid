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
    suspend fun commentOnProduct(productId: Long, text: String)
    suspend fun rateProduct(productId: Long, rating: Int)

    suspend fun buyProduct(productId: Long): ProductDetailResult
}

class ProductDetailRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : ProductDetailRepository {

    override suspend fun getProductDetail(productId: Long): ProductDetailResult {
        try {
            val res: ProductDetailResponseDto = api.getProductDetail(productId)
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

    private suspend fun fetchDetail(productId: Long): ProductDetailResult {
        try {
            val res: ProductDetailResponseDto = api.getProductDetail(productId)
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

    override suspend fun commentOnProduct(productId: Long, text: String) {
        try {
            val body = CommentRequestDto(descripcion = text)
            val res = api.commentProduct(productId, body)
            if (!res.ok) {
                throw Exception(res.message ?: "No se pudo registrar el comentario.")
            }
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun rateProduct(productId: Long, rating: Int) {
        try {
            val body = RatingRequestDto(calificacion = rating)
            val res = api.rateProduct(productId, body)
            if (!res.ok) {
                throw Exception(res.message ?: "No se pudo registrar la calificación.")
            }
            // Si quisieras, podrías usar res.calificacion_promedio para actualizar algo local
        } catch (e: HttpException) {
            throw Exception("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun buyProduct(productId: Long): ProductDetailResult {
        try {
            val res: PurchaseResponseDto = api.buyProduct(productId)
            if (!res.ok) {
                throw Exception(res.message ?: "No se pudo realizar la compra.")
            }
            // Después de comprar, traemos el detalle actualizado
            return fetchDetail(productId)
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
