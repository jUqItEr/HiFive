package com.example.hifive.data

import com.example.hifive.data.model.LoggedInUser
import com.example.hifive.data.model.LoginRequest
import com.example.hifive.ui.activity.LoginActivity
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
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            //val response = RetrofitClient.login(LoginActivity(), LoginRequest(username, password))
            //val user = LoggedInUser("${response.id}","${response.id}")
            // error
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}