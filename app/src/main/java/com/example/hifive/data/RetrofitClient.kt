package com.example.hifive.data

import android.util.Log
import android.widget.Toast
import com.example.hifive.data.model.LoginRequest
import com.example.hifive.data.model.LoginResponse
import com.example.hifive.data.model.RegisterRequest
import com.example.hifive.data.model.RegisterResponse
import com.example.hifive.data.viewmodel.ApiService
import com.example.hifive.ui.activity.LoginActivity
import com.example.hifive.ui.activity.SignupActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val url = "http://hxlab.co.kr:30000"
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val ApiService = retrofit.create(ApiService::class.java)
    fun login(context: LoginActivity, request: LoginRequest): LoginResponse? {
        var loginResponse: LoginResponse? = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.login(request)
                if (response.isSuccessful) {
                    loginResponse = response.body()
                    if (loginResponse?.message == true) {
                        // 로그인 성공 처리
                        //val token = loginResponse.token
                        //val message = loginResponse.token.toString()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, loginResponse.toString(), Toast.LENGTH_SHORT).show()
                        }
                        // 토큰 저장 등의 작업 수행
                    } else {
                        // 로그인 실패 처리
                        val message = loginResponse?.message ?: "로그인 실패"
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "${message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Log.d("success", "${loginResponse.toString()}")
                } else {
                    // 로그인 실패 처리
                    val message = "로그인 실패"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                // 예외 처리
                val message = "로그인 중 오류 발생"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }
        return loginResponse
    }
    fun signUp(context: SignupActivity, request: RegisterRequest): Boolean {
        var success:Boolean = false
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.signUp(request)
                if(response.isSuccessful) {
                    val registerResponse = response.body()
                    if(registerResponse?.success == true)
                        success = true
                    success = true
                } else{

                }
            } catch (e: Exception){
                val message = "통신 오류 발생"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }

        return success
    }
    fun reserveCredit() {}
    // 월별사용내역 request
    fun requestMonthData(month: Int){

    }
}