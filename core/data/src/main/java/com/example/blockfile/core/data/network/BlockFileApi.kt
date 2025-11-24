package com.example.blockfile.core.data.network

import com.example.blockfile.core.model.CatalogResponseDto
import com.example.blockfile.core.model.LoginRequest
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.RankingMejoresCompradoresResponseDto
import com.example.blockfile.core.model.RankingProductosMasCompradosResponseDto
import com.example.blockfile.core.model.RankingProductosMejorCalificadosResponseDto
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





    @GET("apimovil/rankings/productos-mas-comprados/")
    suspend fun getRankingProductosMasComprados(
        @Query("page") page: Int,
    ): RankingProductosMasCompradosResponseDto

    @GET("apimovil/rankings/mejores-compradores/")
    suspend fun getRankingMejoresCompradores(
        @Query("page") page: Int,
    ): RankingMejoresCompradoresResponseDto

    @GET("apimovil/rankings/productos-mejor-calificados/")
    suspend fun getRankingProductosMejorCalificados(
        @Query("page") page: Int,
    ): RankingProductosMejorCalificadosResponseDto
}