package com.example.blockfile.core.domain.product

import com.example.blockfile.core.data.repository.ProductDetailRepository
import javax.inject.Inject

class CommentOnProductUseCase @Inject constructor(
    private val repository: ProductDetailRepository,
) {
    suspend operator fun invoke(productId: Long, text: String) {
        repository.commentOnProduct(productId, text)
    }
}
