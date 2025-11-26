package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsRepository
import com.example.blockfile.core.model.AdminProductDetail
import javax.inject.Inject

class SaveAdminProductUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(detail: AdminProductDetail): Long =
        repository.saveProduct(detail)
}
