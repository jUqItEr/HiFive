package com.example.hifive.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.hifive.R
import com.example.hifive.data.Result
import com.example.hifive.data.RetrofitClient
import com.example.hifive.databinding.ActivityLoginBinding
import com.example.hifive.login.LoggedInUserView
import com.example.hifive.login.LoginViewModel
import com.example.hifive.login.LoginViewModelFactory
import com.example.hifive.ui.base.BaseActivity
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
        initObservers()

        val username = binding.username
        val password = binding.password
        val login = binding.login
        val loading = binding.loading

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> lifecycleScope.launch {
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                    }
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val result =
                        loginViewModel.login(username.text.toString(), password.text.toString())


                    if (result is Result.Success) {
                        // input clear
                        username.text = null
                        password.text = null

                        val bundle = Bundle().apply {
                            putString("id", result.data.userId)
                            putString("name", result.data.displayName)
                        }
                        //조건 필요
                        val intent: Intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("user", bundle)
                        startActivity(intent)
                    } else {

                    }
                }
            }
        }
        //
        binding.signin.setOnClickListener {
            val intent: Intent = Intent(this, SignupActivity::class.java) //error
            startActivity(intent)
        }

        binding.findButton.setOnClickListener {
            val intent: Intent = Intent(this, findIDPWActivity::class.java) //error
            startActivity(intent)
        }
        //
    }

    private var backPressedTime: Long = 0
    private val BACK_KEY_TIME_INTERVAL: Long = 2000 // 2초

    override fun onBackPressed() {
        if (backPressedTime + BACK_KEY_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // 앱의 모든 엑티비티 종료
        } else {
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    // Init components
    private fun initComponents() = with(binding) {

    }

    private fun initObservers() {

    }

    companion object {
        private const val tag = "LoginActivity"
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }


}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}