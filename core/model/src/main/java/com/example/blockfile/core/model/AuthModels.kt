package com.example.blockfile.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val nombre: String,
    val contrasena: String,
)

@Serializable
data class LoginResponse(
    val id_usuario: Long,
    val nombre_usuario: String,
    val correo: String,
    val saldo: String,
    val excliente: Boolean,
)

@Serializable
data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
)

@Serializable
data class RegisterResponse(
    val id_usuario: Long,
    val nombre_usuario: String,
    val correo: String,
    val saldo: String,
    val excliente: Boolean,
)
