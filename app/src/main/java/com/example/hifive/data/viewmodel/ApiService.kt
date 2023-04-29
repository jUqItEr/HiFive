package com.example.hifive.data.viewmodel

import com.example.hifive.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/users/register")
    suspend fun signUp(
        @Body request: RegisterRequest
    ): Response<CommonResponse>

    @POST("/auth")
    suspend fun auth(
        @Body name: String,
        @Body phone : String
    ): Response<Boolean>

    @POST("/pw/auth")
    suspend fun pwAuth(
        @Body id: String,
        @Body name: String,
        @Body phone : String
    ): Response<Boolean>

    @POST("/auth/verify")
    suspend fun auth_verify(
        @Body phone: String,
        @Body request: Int
    ): Response<Boolean>

    @PUT("/users/change-pwd")
    suspend fun change_pwd(
        @Body request: IDPWmodify
    ): Response<CommonResponse>
}