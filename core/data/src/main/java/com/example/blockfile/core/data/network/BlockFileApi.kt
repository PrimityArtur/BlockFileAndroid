package com.example.blockfile.core.data.network

import com.example.blockfile.core.model.LoginRequest
import com.example.blockfile.core.model.LoginResponse
import com.example.blockfile.core.model.RegisterRequest
import com.example.blockfile.core.model.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BlockFileApi {

    @POST("apimovil/login/")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("apimovil/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}