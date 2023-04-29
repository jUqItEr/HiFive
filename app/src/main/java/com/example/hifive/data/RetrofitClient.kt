package com.example.hifive.data

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.hifive.R
import com.example.hifive.data.model.*
import com.example.hifive.data.viewmodel.ApiService
import com.example.hifive.ui.activity.LoginActivity
import com.example.hifive.ui.activity.MonthlyListActivity
import com.example.hifive.ui.activity.SignupActivity
import kotlinx.coroutines.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

object RetrofitClient {
    private val url = "http://hxlab.co.kr:30000"
    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val ApiService = retrofit.create(ApiService::class.java)
    /*
    fun login(context: LoginActivity, request: LoginRequest): LoginResponse? {
        var job = CoroutineScope(Dispatchers.IO).async {
            try {
                val response = ApiService.login(request)
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.success == true) {
                        // 로그인 성공 처리
                        //val token = loginResponse.token
                        //val message = loginResponse.token.toString()

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, loginResponse.toString(), Toast.LENGTH_SHORT).show()
                        }
                        // 토큰 저장 등의 작업 수행
                    } else {
                        // 로그인 실패 처리
                        val message = loginResponse?.success ?: "로그인 실패"
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "${message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@async loginResponse
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
        //val response = job.await()
        return respone
    }
    */

    //회원가입 기능
    suspend fun signUp(context: SignupActivity, request: RegisterRequest): Boolean {
        var success:Boolean = false
            try {
                val response = ApiService.signUp(request)
                //Log.d("회원가입 상태", response.toString())
                if(response.isSuccessful) {
                    Log.d("1", "response successful")
                    val registerResponse = response.body()
                    if(registerResponse?.success == true) {
                        Log.d("2", "response success true")
                        success = true
                        Log.d("2-2", "${success}")
                    }
                    else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                        }
                        Log.d("3", "response success false")
                    }
                } else{
                    Log.d("4", "response fail")
                }

            } catch (e: Exception){
                val message = "통신 오류 발생"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        Log.d("5", "${success}")
        return success
    }

    fun reserveCredit() {

    }
    // 월별사용내역 request
    fun requestMonthData(context: MonthlyListActivity, request: SpentListRequest){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                //val response = ApiService//수정
                if(/*response.isSuccessful*/true){
                    // response.body()
                    // todo 값 받아오기

                }
            } catch (e: Exception){
                val message = "통신 오류 발생"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }

        //return
    }

    fun requestAuth(context: Activity, phone: String, name: String) {
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = ApiService.auth(name, phone)
                if(response.body() == true){
                    val message = "인증번호를 발송 했습니다"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "요청 실패", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception){
                val message = "요청 실패"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }
    }
    fun requestPwAuth(context: Activity, id: String, name: String, phone: String) {
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = ApiService.pwAuth(id, name, phone)
                if(response.isSuccessful){
                    val message = "인증번호를 발송 했습니다"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "요청 실패", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception){
                val message = "요청 실패"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }
    }
    fun requestVerify(context: SignupActivity, phone: String, request: Int){
        CoroutineScope(Dispatchers.IO).launch{
            try{
                val response = ApiService.auth_verify(phone, request)
                if(response.isSuccessful){
                    val message = "인증 되었습니다."
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else{
                    val message = "확인코드가 일치하지 않습니다."
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }


            } catch (e: Exception){
                val message = "요청 실패"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }
    }


    //비밀번호 수정 기능
    fun changePWD(context: Activity, id: String, pwd: String){
        // 비밀번호 교체
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.change_pwd(IDPWmodify(id, pwd))
                if(response.isSuccessful) {
                    val message = response.body()?.message
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        context.finish()
                    }
                }
            } catch (e: Exception){
                val message = "요청 실패"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                e.message?.let { Log.d(message, it) }
            }
        }
    }
}