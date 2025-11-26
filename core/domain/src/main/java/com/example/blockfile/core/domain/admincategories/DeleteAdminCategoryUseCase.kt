package com.example.blockfile.core.domain.admincategories

import com.example.blockfile.core.data.repository.AdminCategoriesRepository
import javax.inject.Inject

class DeleteAdminCategoryUseCase @Inject constructor(
    private val repository: AdminCategoriesRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteCategory(id)
    }
}
