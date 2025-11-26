package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsRepository
import javax.inject.Inject

class AddProductImageUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(idProducto: Long, bytes: ByteArray, filename: String, orden: Int? = null) {
        repository.addProductImage(idProducto, bytes, filename, orden)
    }
}
