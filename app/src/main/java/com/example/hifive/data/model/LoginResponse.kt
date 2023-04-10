package com.example.hifive.data.model

data class LoginResponse(
    val message: Boolean,
    val id: String,
    val pwd: String,
    val hand: String
)
