package com.example.hifive.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val url = "https://hxlab.co.kr:30000"
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun login() {}
    fun signUp() {}
    fun reserveCredit() {}
    // 월별사용내역 request
    fun requestMonthData(month: Int){

    }
}