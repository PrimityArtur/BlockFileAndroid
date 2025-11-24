package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.LoginRequest
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.RegisterRequest
import com.example.blockfile.core.model.RegisterResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface AuthRepository {
    suspend fun login(nombre: String, contrasena: String): LoginResponse
    suspend fun register(nombre: String, correo: String, contrasena: String): RegisterResponse
}

class AuthRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : AuthRepository {

    override suspend fun login(nombre: String, contrasena: String): LoginResponse {
        try {
            return api.login(LoginRequest(nombre = nombre, contrasena = contrasena))
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    override suspend fun register(nombre: String, correo: String, contrasena: String): RegisterResponse {
        try {
            return api.register(RegisterRequest(nombre = nombre, correo = correo, contrasena = contrasena))
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    private fun parseErrorBody(e: HttpException): String {
        val rawBody = e.response()?.errorBody()?.string()
        return rawBody ?: "Error del servidor (${e.code()})."
    }
}
