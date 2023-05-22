package com.example.hifive.data

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.hifive.data.model.*
import com.example.hifive.data.viewmodel.ApiService
import com.example.hifive.ui.activity.LoginActivity
import com.example.hifive.ui.activity.SignupActivity
import kotlinx.coroutines.*
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

    //로그인
    suspend fun login(request: LoginRequest): LoginResponse? {
        var loginResponse: LoginResponse? = null

        withContext(Dispatchers.IO) {
            try {
                val response = ApiService.login(request)
                // ... 기타 로그와 처리 ...

                if (response.isSuccessful) {
                    loginResponse = response.body()
                    // ... 기타 로그와 처리 ...

                    if (loginResponse?.success == true) {
                        //Log.d("login response success ???", "${loginResponse.message}"+"+"+"$success")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(LoginActivity(), loginResponse.toString(), Toast.LENGTH_SHORT).show()
                            return@withContext loginResponse
                        }
                        // ... 로그인 성공 처리 ...
                    } else {
                        // ... 로그인 실패 처리 ...
                        val message = loginResponse?.success ?: "로그인 실패"
                        withContext(Dispatchers.Main) {
                            //Toast.makeText(LoginActivity(), "${message}", Toast.LENGTH_SHORT).show()
                            return@withContext loginResponse
                        }
                    }
                } else {
                    // ... 로그인 실패 처리 ...
                    val message = "로그인 실패"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(LoginActivity(), message, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // ... 예외 처리 ...
            }
        }
        return loginResponse
    }


    //register error
    //회원가입 기능
    @SuppressLint("SuspiciousIndentation")
    suspend fun signUp(context: SignupActivity, request: RegisterRequest): Boolean {
        var success:Boolean = false
            try {
                val response = ApiService.signUp(request)
                //Log.d("회원가입 상태", response.toString())
                if(response.isSuccessful) {
                    Log.d("1", "response successful")
                    val registerResponse = response.body()
                    Log.d("반환값 확인","$registerResponse")
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
    suspend fun requestMonthData(request: SpentListRequest): PayListResponse?{
        var payListResponse : PayListResponse? = null
        try {
            val response = ApiService.getPayList(request.id, request.year, request.month)
            //val response = ApiService.getPayList(request)
            if(response.isSuccessful){
                payListResponse = response.body()
                // todo 값 받아오기
                Log.d("pay list", "${payListResponse.toString()}")
            }
        } catch (e: Exception){
            val message = "통신 오류 발생"
            e.message?.let { Log.e(message, it) }
        } finally {
            return payListResponse
        }
    }

    // 월별 총계 함수
    suspend fun requestTotalPrice(request: SpentListRequest) :Int {
        var total = 0
        try{
            val response = ApiService.getPayList(request.id, request.year, request.month)
            //val response = ApiService.getPayList(request)
            if(response.isSuccessful) {
                val payListResponse = response.body()
                if (payListResponse != null) {
                    total = payListResponse.total
                } else{
                    Log.e("payList NULL", "${payListResponse}")
                }
            } else{
                Log.e("response error", "${response}")
            }
        } catch (e: Exception){
            e.message?.let { Log.e("통신 오류 발생", it) }
        } finally {
            return total
        }
    }

    // 인증번호 요청
    // todo server test
    suspend fun requestAuth(email: String): CommonResponse?{
        var result: CommonResponse? = null
        try{
            val response = ApiService.auth(AuthRequest(email))
            Log.d("response", "${response}")
            if(response.isSuccessful){
                result = response.body()
            } else{
                Log.e("response error", "requestAuth response fail")
            }
        } catch (e: Exception){
            val message = "요청 실패"
            Log.e(message, "${e.message}")
        } finally {
            return result
        }
    }

//    fun requestPwAuth(context: Activity, id: String, name: String, phone: String) {
//        CoroutineScope(Dispatchers.IO).launch{
//            try{
//                val response = ApiService.pwAuth(id, name, phone)
//                if(response.isSuccessful){
//                    val message = "인증번호를 발송 했습니다"
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
//                    }
//                } else{
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "요청 실패", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//            } catch (e: Exception){
//                val message = "요청 실패"
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//                }
//                e.message?.let { Log.d(message, it) }
//            }
//        }
//    }


    suspend fun requestVerify(email: String, number: String): CommonResponse?{
        var result : CommonResponse? = null
        try{
            val response = ApiService.auth_verify(AuthVerifyRequest(email, number))
            if(response.isSuccessful){
                result = response.body()
            } else{
                Log.e("response error", "AuthVerify response fail")
            }
        } catch (e: Exception){
            Log.e("auth verify exception", "${e.message}")
        } finally {
            return result
        }
    }


    //아이디찾기
    suspend fun requestFindId(request:FindIdRequest):FindIdResponse?{
        var findIdResponse:FindIdResponse?=null
        try{
            val response = ApiService.find_id(request.name,request.email,request.certification)
            Log.d("제발요오오오오오","$response")
            if(response.isSuccessful){
                findIdResponse=response.body()
                Log.d("test","id찾기 테스트 ${findIdResponse}")
            }

        }catch (e:Exception){
            val message = "통신 오류 발생"
//            withContext(Dispatchers.Main) {
//                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
//            }
            e.message?.let { Log.d(message, it) }
        }finally {
            return findIdResponse
        }
    }

    //비밀번호 수정 기능
    fun changePWD(context: Activity, id: String, pwd: String, certification: Boolean){
        // 비밀번호 교체
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiService.change_pwd(IDPWdata(id, pwd,certification))
                if(response.isSuccessful) {
                    val message = response.body()?.message
                    val success=response.body()?.success
                    if(success==true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            context.finish()
                        }
                    }else if(success==false){
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
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

    // 회원탈퇴
    fun deleteUser(context: Activity, id: String, pwd: String){
        CoroutineScope(Dispatchers.IO).launch {
            try{
                val response = ApiService.delete_user(DeletUserData(id, pwd))
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