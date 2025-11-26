package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.*
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.RequestBody

data class AdminProductsPage(
    val items: List<AdminProductItem>,
    val page: Int,
    val totalPages: Int,
)

interface AdminProductsRepository {
    suspend fun getAdminProductsPage(
        page: Int,
        id: Long?,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): AdminProductsPage

    suspend fun getProductDetail(id: Long): AdminProductDetail

    suspend fun saveProduct(detail: AdminProductDetail): Long

    suspend fun uploadProductFile(idProducto: Long, bytes: ByteArray, filename: String)

    suspend fun addProductImage(idProducto: Long, bytes: ByteArray, filename: String, orden: Int? = null)

    suspend fun reorderProductImage(idImagen: Long, orden: Int)

    suspend fun deleteProductImage(idImagen: Long)
}

class AdminProductsRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : AdminProductsRepository {

    override suspend fun getAdminProductsPage(
        page: Int,
        id: Long?,
        nombre: String?,
        autor: String?,
        categoria: String?,
    ): AdminProductsPage {
        val response = api.getAdminProducts(
            page = page,
            id = id,
            nombre = nombre?.takeIf { it.isNotBlank() },
            autor = autor?.takeIf { it.isNotBlank() },
            categoria = categoria?.takeIf { it.isNotBlank() },
        )

        val items = response.rows.map { it.toDomainItem() }

        return AdminProductsPage(
            items = items,
            page = response.page,
            totalPages = response.total_pages,
        )
    }

    override suspend fun getProductDetail(id: Long): AdminProductDetail {
        val dto = api.getAdminProductDetail(id)
        return dto.toDomainDetail()
    }

    override suspend fun saveProduct(detail: AdminProductDetail): Long {
        val body = SaveAdminProductRequestDto(
            id = detail.id,
            nombre = detail.nombre,
            descripcion = detail.descripcion,
            version = detail.version,
            precio = detail.precio,
            id_autor = detail.autorId,
            id_categoria = detail.categoriaId,
            activo = detail.activo,
        )
        val resp = api.saveAdminProduct(body)
        return resp.id
    }

    override suspend fun uploadProductFile(idProducto: Long, bytes: ByteArray, filename: String) {
        val idBody: RequestBody = idProducto.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val fileBody = bytes.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("archivo", filename, fileBody)

        api.uploadAdminProductFile(
            idProducto = idBody,
            archivo = part,
        )
    }

    override suspend fun addProductImage(idProducto: Long, bytes: ByteArray, filename: String, orden: Int?) {
        val idBody: RequestBody = idProducto.toString()
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val ordenBody: RequestBody? = orden?.toString()
            ?.toRequestBody("text/plain".toMediaTypeOrNull())

        val fileBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("archivo", filename, fileBody)

        api.addAdminProductImage(
            idProducto = idBody,
            orden = ordenBody,
            archivo = part,
        )
    }

    override suspend fun reorderProductImage(idImagen: Long, orden: Int) {
        api.reorderAdminProductImage(
            idImagen = idImagen,
            orden = orden,
        )
    }

    override suspend fun deleteProductImage(idImagen: Long) {
        api.deleteAdminProductImage(idImagen = idImagen)
    }
}

/* ---------------- MAPPERS ---------------- */

private fun AdminProductItemDto.toDomainItem(): AdminProductItem =
    AdminProductItem(
        id = id,
        nombre = nombre,
        autor = autor,
        categoria = categoria,
        promedio = promedio,
    )

private fun AdminProductDetailDto.toDomainDetail(): AdminProductDetail =
    AdminProductDetail(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        version = version,
        precio = precio,
        autorId = autor_id,
        categoriaId = categoria_id,
        activo = activo,
        tieneArchivo = tiene_archivo,
        imagenes = imagenes.map {
            AdminProductImage(
                id = it.id,
                orden = it.orden,
                url = it.url,
            )
        },
    )