package com.example.blockfile.core.domain.admincategories

import com.example.blockfile.core.data.repository.AdminCategoriesPage
import com.example.blockfile.core.data.repository.AdminCategoriesRepository
import javax.inject.Inject

class GetAdminCategoriesPageUseCase @Inject constructor(
    private val repository: AdminCategoriesRepository,
) {
    suspend operator fun invoke(
        page: Int,
        id: Long?,
        nombre: String?,
        descripcion: String?,
    ): AdminCategoriesPage = repository.getAdminCategoriesPage(
        page = page,
        id = id,
        nombre = nombre,
        descripcion = descripcion,
    )
}
