package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsRepository
import javax.inject.Inject

class DeleteProductImageUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(idImagen: Long) {
        repository.deleteProductImage(idImagen)
    }
}
