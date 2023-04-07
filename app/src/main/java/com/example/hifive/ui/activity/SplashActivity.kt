package com.example.hifive.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.hifive.data.viewmodel.SplashViewModel
import com.example.hifive.databinding.ActivitySplashBinding
import com.example.hifive.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // On Android SDK 33
                // This will be ignored user back pressed button click.
            }
        }
        val splashScreen = installSplashScreen()

        splashScreen.apply {
            setKeepOnScreenCondition {
                viewModel.isLoading.value
            }
        }
        onBackPressedDispatcher.apply {
            addCallback(callback)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val nextIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startActivity(nextIntent)
            finish()
        }, duration)
    }

    companion object {
        private const val tag: String = "SplashActivity"
        private const val duration: Long = 1500
    }
}