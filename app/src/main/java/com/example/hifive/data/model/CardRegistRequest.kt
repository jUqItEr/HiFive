package com.example.hifive.data.model

data class CardRegistRequest(
    val card_number:String,
    val expiry:String,
    val birth:String,
    val pwd_2digit:String,
    val id:String,
    val card_name:String,
    val certification:Boolean
)
