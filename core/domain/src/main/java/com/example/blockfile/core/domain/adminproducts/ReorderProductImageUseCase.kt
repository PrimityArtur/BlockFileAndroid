package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsRepository
import javax.inject.Inject

class ReorderProductImageUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(idImagen: Long, orden: Int) {
        repository.reorderProductImage(idImagen, orden)
    }
}
