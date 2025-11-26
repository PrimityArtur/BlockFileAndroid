package com.example.blockfile.core.domain.adminusers

import com.example.blockfile.core.data.repository.AdminUsersRepository
import com.example.blockfile.core.model.AdminUserDetail
import javax.inject.Inject

class GetAdminUserDetailUseCase @Inject constructor(
    private val repository: AdminUsersRepository,
) {
    suspend operator fun invoke(id: Long): AdminUserDetail =
        repository.getUserDetail(id)
}
