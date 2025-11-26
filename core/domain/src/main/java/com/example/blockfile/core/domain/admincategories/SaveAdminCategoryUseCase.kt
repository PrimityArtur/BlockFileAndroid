package com.example.blockfile.core.domain.admincategories

import com.example.blockfile.core.data.repository.AdminCategoriesRepository
import javax.inject.Inject

class SaveAdminCategoryUseCase @Inject constructor(
    private val repository: AdminCategoriesRepository,
) {
    suspend operator fun invoke(
        id: Long?,
        nombre: String,
        descripcion: String,
    ): Long = repository.saveCategory(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
    )
}
