package com.example.blockfile.core.data.network

import com.example.blockfile.core.model.CatalogResponseDto
import com.example.blockfile.core.model.LoginRequest
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.RegisterRequest
import com.example.blockfile.core.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BlockFileApi {

    @POST("apimovil/login/")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("apimovil/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse



    @GET("apimovil/catalogo/")
    suspend fun getCatalog(
        @Query("page") page: Int,
        @Query("nombre") nombre: String? = null,
        @Query("autor") autor: String? = null,
        @Query("categoria") categoria: String? = null,
    ): CatalogResponseDto
}