package com.example.hifive.data.model

data class PayListResponse(
    val success: Boolean,
    val data: List<PayData>,
    val total: Int
)
