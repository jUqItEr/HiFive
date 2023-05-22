package com.example.hifive.data.viewmodel

import com.example.hifive.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    //로그인 요청, 수정요망
    @POST("/users/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // 회원가입 요청, 수정요망
    @POST("/users/register")
    suspend fun signUp(
        @Body request: RegisterRequest
    ): Response<CommonResponse>

    // 인증번호 요청
    @POST("/users/auth")
    suspend fun auth(
        @Body request: AuthRequest
    ): Response<CommonResponse>

    // 비밀번호 수정 인증번호 요청, 수정요망
//    @POST("/pw/auth")
//    suspend fun pwAuth(
//        @Body id: String,
//        @Body name: String,
//        @Body phone : String
//    ): Response<Boolean>

    // 검증 요청
    @POST("/users/auth-check")
    suspend fun auth_verify(
        @Body request : AuthVerifyRequest
    ): Response<CommonResponse>

    //아이디 찾기
    @GET("/users/find-id")
    suspend fun find_id(
        @Query("name") name:String,
        @Query("email") email:String,
        @Query("certification") certification:String
    ):Response<FindIdResponse>

    // 비밀번호 수정
    @PUT("/users/change-pwd")
    suspend fun change_pwd(
        @Body request: IDPWdata
    ): Response<CommonResponse>

    // 화윈 삭제
    @HTTP(method = "DELETE", path = "/users/delete", hasBody = true)
    suspend fun delete_user(
        @Body request: IDPWdata
    ): Response<CommonResponse>

    // 결제 내역 리스트 호출
    @GET("/users/pay-list")
    suspend fun getPayList(
        @Query("id") id: String,
        @Query("year") year: String,
        @Query("month") month: String
    ): Response<PayListResponse>


}