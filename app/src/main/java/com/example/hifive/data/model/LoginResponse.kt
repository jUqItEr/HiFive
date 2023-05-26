package com.example.hifive.data.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val name: String,
    val email:String
)
