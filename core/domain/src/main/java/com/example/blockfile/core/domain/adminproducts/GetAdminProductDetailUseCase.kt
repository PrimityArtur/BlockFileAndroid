package com.example.blockfile.core.domain.adminproducts

import com.example.blockfile.core.data.repository.AdminProductsRepository
import com.example.blockfile.core.model.AdminProductDetail
import javax.inject.Inject

class GetAdminProductDetailUseCase @Inject constructor(
    private val repository: AdminProductsRepository,
) {
    suspend operator fun invoke(id: Long): AdminProductDetail =
        repository.getProductDetail(id)
}
