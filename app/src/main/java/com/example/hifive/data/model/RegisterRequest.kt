package com.example.hifive.data.model

data class RegisterRequest(
    val id: String,
    val pwd: String,
    val email:String,
    val name: String,
    val birth:String,
    val certification:Boolean

)
