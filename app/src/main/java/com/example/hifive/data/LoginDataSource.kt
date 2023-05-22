package com.example.hifive.data

import android.util.Log
import com.example.hifive.data.model.LoggedInUser
import com.example.hifive.data.model.LoginRequest
import kotlinx.coroutines.awaitAll
import java.io.IOException
import java.security.MessageDigest

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: 오류 수정
            //error null
            val response = RetrofitClient.login(LoginRequest(username, encrypt(password)))
            if(response !== null) {
                if (response?.success==true) {
                    val user = LoggedInUser("${username}", "${username}")
                    return Result.Success(user)
                } else {
                    return Result.Error(Exception("로그인 오류"))
                }
            }else{
                return Result.Error(Exception("로그인 오류"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    //비밀번호 암호화
    private fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}