package com.example.blockfile.core.data.repository

import com.example.blockfile.core.data.network.BlockFileApi
import com.example.blockfile.core.model.AdminProfileDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface AdminProfileRepository {
    suspend fun updateProfile(dto: AdminProfileDto): AdminProfileDto
}

class AdminProfileRepositoryImpl @Inject constructor(
    private val api: BlockFileApi,
) : AdminProfileRepository {

    override suspend fun updateProfile(dto: AdminProfileDto): AdminProfileDto {
        try {
            return api.updateAdminProfile(dto)
        } catch (e: HttpException) {
            throw Exception(parseErrorBody(e))
        } catch (e: IOException) {
            throw Exception("Error de conexión. Verifica tu internet.")
        }
    }

    private fun parseErrorBody(e: HttpException): String {
        val rawBody = e.response()?.errorBody()?.string()
            ?: return "Error del servidor (${e.code()})."

        return try {
            val json = org.json.JSONObject(rawBody)

            when {
                json.has("error") -> json.getString("error")
                json.has("errors") -> json.getString("errors")
                json.has("detail") -> json.getString("detail")
                else -> rawBody
            }
        } catch (_: Exception) {
            rawBody
        }
    }
}
