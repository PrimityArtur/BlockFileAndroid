package com.example.blockfile.core.data.network

import com.example.blockfile.core.model.ActualizarPerfilRequestDto
import com.example.blockfile.core.model.AdminCategoriesResponseDto
import com.example.blockfile.core.model.AdminProductDetailDto
import com.example.blockfile.core.model.AdminProductsResponseDto
import com.example.blockfile.core.model.AdminProfileDto
import com.example.blockfile.core.model.CatalogResponseDto
import com.example.blockfile.core.model.CommentRequestDto
import com.example.blockfile.core.model.ComprasClienteResponseDto
import com.example.blockfile.core.model.LoginRequest
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.PerfilClienteResponseDto
import com.example.blockfile.core.model.ProductDetailResponseDto
import com.example.blockfile.core.model.PurchaseResponseDto
import com.example.blockfile.core.model.RankingMejoresCompradoresResponseDto
import com.example.blockfile.core.model.RankingProductosMasCompradosResponseDto
import com.example.blockfile.core.model.RankingProductosMejorCalificadosResponseDto
import com.example.blockfile.core.model.RatingRequestDto
import com.example.blockfile.core.model.RegisterRequest
import com.example.blockfile.core.model.RegisterResponse
import com.example.blockfile.core.model.SaveAdminProductRequestDto
import com.example.blockfile.core.model.SaveAdminProductResponseDto
import com.example.blockfile.core.model.SimpleResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

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


    @GET("apimovil/productos/{id}/")
    suspend fun getProductDetail(
        @Path("id") id: Long,
    ): ProductDetailResponseDto


    @Streaming
    @GET("apimovil/productos/{id}/descargar/")
    suspend fun downloadProduct(
        @Path("id") id: Long,
    ): Response<ResponseBody>

    @POST("apimovil/productos/{id}/comentar/")
    suspend fun commentProduct(
        @Path("id") id: Long,
        @Body body: CommentRequestDto,
    ): SimpleResponseDto

    @POST("apimovil/productos/{id}/calificar/")
    suspend fun rateProduct(
        @Path("id") id: Long,
        @Body body: RatingRequestDto,
    ): SimpleResponseDto


    @POST("apimovil/productos/{id}/comprar/")
    suspend fun buyProduct(
        @Path("id") id: Long,
    ): PurchaseResponseDto




    @GET("apimovil/perfil/")
    suspend fun getPerfilCliente(): PerfilClienteResponseDto

    @POST("apimovil/perfil/actualizar/")
    suspend fun actualizarPerfilCliente(
        @Body body: ActualizarPerfilRequestDto,
    ): PerfilClienteResponseDto

    @GET("apimovil/perfil/compras/")
    suspend fun getComprasCliente(
        @Query("page") page: Int,
    ): ComprasClienteResponseDto



    @GET("apimovil/admin/perfil/")
    suspend fun getAdminProfile(
        @Query("id_usuario") idUsuario: Long
    ): AdminProfileDto

    @POST("apimovil/admin/perfil/")
    suspend fun updateAdminProfile(
        @Body body: AdminProfileDto
    ): AdminProfileDto


    @GET("apimovil/admin/productos/")
    suspend fun getAdminProducts(
        @Query("page") page: Int,
        @Query("id") id: Long? = null,
        @Query("nombre") nombre: String? = null,
        @Query("autor") autor: String? = null,
        @Query("categoria") categoria: String? = null,
    ): AdminProductsResponseDto

    @GET("apimovil/admin/productos/detalle/{id}/")
    suspend fun getAdminProductDetail(
        @Path("id") id: Long,
    ): AdminProductDetailDto

    @POST("apimovil/admin/productos/guardar/")
    suspend fun saveAdminProduct(
        @Body body: SaveAdminProductRequestDto,
    ): SaveAdminProductResponseDto

    @Multipart
    @POST("apimovil/admin/productos/archivo/")
    suspend fun uploadAdminProductFile(
        @Part("id_producto") idProducto: RequestBody,
        @Part archivo: MultipartBody.Part,
    ): SimpleResponseDto

    @Multipart
    @POST("apimovil/admin/productos/imagenes/agregar/")
    suspend fun addAdminProductImage(
        @Part("id_producto") idProducto: RequestBody,
        @Part("orden") orden: RequestBody?,
        @Part archivo: MultipartBody.Part,
    ): SimpleResponseDto

    @FormUrlEncoded
    @POST("apimovil/admin/productos/imagenes/reordenar/")
    suspend fun reorderAdminProductImage(
        @Field("id_imagen") idImagen: Long,
        @Field("orden") orden: Int,
    ): SimpleResponseDto

    @FormUrlEncoded
    @POST("apimovil/admin/productos/imagenes/borrar/")
    suspend fun deleteAdminProductImage(
        @Field("id_imagen") idImagen: Long,
    ): SimpleResponseDto

    // ================== ADMIN CATEGORÍAS (MÓVIL) ==================

    @GET("apimovil/admin/categorias/")
    suspend fun getAdminCategoriesPage(
        @Query("page") page: Int,
        @Query("id") id: String? = null,
        @Query("nombre") nombre: String? = null,
        @Query("descripcion") descripcion: String? = null,
    ): AdminCategoriesResponseDto

}