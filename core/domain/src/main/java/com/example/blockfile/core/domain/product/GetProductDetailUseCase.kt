package com.example.blockfile.core.domain.product

import com.example.blockfile.core.data.repository.ProductDetailRepository
import com.example.blockfile.core.data.repository.ProductDetailResult
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(
    private val repository: ProductDetailRepository,
) {
    suspend operator fun invoke(productId: Long): ProductDetailResult =
        repository.getProductDetail(productId)
}
