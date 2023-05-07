package com.example.hifive.data

import android.util.Log
import com.example.hifive.data.model.LoggedInUser
import com.example.hifive.data.model.LoginRequest
import com.example.hifive.data.model.LoginResponse
import com.example.hifive.ui.activity.LoginActivity
import com.example.hifive.ui.activity.findIDPWActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: 오류 수정
            //error null
            val response = RetrofitClient.login(LoginRequest(username, password))

            Log.d("response", "${response?.success}"+" / "+"${response.toString()}")
            //if(response?.success == true) {
            if(true) {
                val user = LoggedInUser("${username}", "${username}")
                return Result.Success(user)
            } else{
                return Result.Error(Exception("로그인 오류"))
            }
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}