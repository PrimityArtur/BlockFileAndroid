package com.example.blockfile.core.domain.auth

import com.example.blockfile.core.data.repository.AuthRepository
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.RegisterResponse
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(nombre: String, contrasena: String): LoginResponse =
        repository.login(nombre, contrasena)
}

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(nombre: String, correo: String, contrasena: String): RegisterResponse =
        repository.register(nombre, correo, contrasena)
}
