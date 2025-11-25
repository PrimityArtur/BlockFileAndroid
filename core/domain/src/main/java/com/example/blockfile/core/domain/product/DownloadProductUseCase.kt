package com.example.blockfile.core.domain.product

import com.example.blockfile.core.data.repository.ProductFileRepository
import java.io.File
import javax.inject.Inject

class DownloadProductUseCase @Inject constructor(
    private val repository: ProductFileRepository,
) {
    suspend operator fun invoke(productId: Long): File =
        repository.downloadProduct(productId)
}
