package com.example.blockfile.core.domain.profile

import com.example.blockfile.core.data.repository.ComprasPerfilPage
import com.example.blockfile.core.data.repository.ProfileRepository
import com.example.blockfile.core.model.PerfilCliente
import javax.inject.Inject

class GetPerfilClienteUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): PerfilCliente = repository.getProfile()
}

class UpdatePerfilClienteUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(
        nombre: String,
        correo: String,
        contrasena: String?,
    ): PerfilCliente = repository.updateProfile(nombre, correo, contrasena)
}

class GetComprasPerfilUseCase @Inject constructor(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(page: Int): ComprasPerfilPage =
        repository.getCompras(page)
}
