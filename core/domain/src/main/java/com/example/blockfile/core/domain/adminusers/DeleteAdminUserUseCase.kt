package com.example.blockfile.core.domain.adminusers

import com.example.blockfile.core.data.repository.AdminUsersRepository
import javax.inject.Inject

class DeleteAdminUserUseCase @Inject constructor(
    private val repository: AdminUsersRepository,
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteUser(id)
    }
}
