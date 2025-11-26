package com.example.blockfile.core.domain.adminusers

import com.example.blockfile.core.data.repository.AdminUsersPage
import com.example.blockfile.core.data.repository.AdminUsersRepository
import javax.inject.Inject

class GetAdminUsersPageUseCase @Inject constructor(
    private val repository: AdminUsersRepository,
) {
    suspend operator fun invoke(
        page: Int,
        id: Long?,
        nombre: String?,
        saldo: String?,
    ): AdminUsersPage = repository.getAdminUsersPage(
        page = page,
        id = id,
        nombre = nombre,
        saldo = saldo,
    )
}
