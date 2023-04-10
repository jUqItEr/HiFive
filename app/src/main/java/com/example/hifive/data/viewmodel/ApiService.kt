package com.example.hifive.data.viewmodel

import com.example.hifive.data.model.LoginRequest
import com.example.hifive.data.model.LoginResponse
import com.example.hifive.data.model.RegisterRequest
import com.example.hifive.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("/users/register")
    suspend fun signUp(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>
}