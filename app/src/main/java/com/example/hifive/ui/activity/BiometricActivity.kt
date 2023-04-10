package com.example.hifive.ui.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.CryptoObject
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.hifive.databinding.ActivityBiometricBinding
import com.example.hifive.ui.base.BaseActivity
import java.util.concurrent.Executor

class BiometricActivity :
    BaseActivity<ActivityBiometricBinding>(ActivityBiometricBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()

        // Initialize biometric features
        createBiometricPrompt()
        createPromptInfo()
    }

    override fun onResume() {
        super.onResume()

        requestAuthenticate()
    }

    private fun initComponents() = with(binding) {

    }

    /**
     * Biometric Authentication Functions
     *
     * @author: Kiseok Kang (Github: @jUqItEr)
     * */
    private var biometricPrompt: BiometricPrompt? = null
    private var executor: Executor? = null
    private var promptInfo: PromptInfo? = null

    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d(tag, "[Variables]: requestLauncher= $result")

            if (result.resultCode == Activity.RESULT_OK) {
                requestAuthenticate()
            }
        }

    private fun createBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this)

        executor?.let {
            biometricPrompt = BiometricPrompt(
                this@BiometricActivity,
                it,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int, errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)

                        Toast.makeText(
                            this@BiometricActivity,
                            "Error: $errorCode",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Log.d(tag, "Failed :(")
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        result.cryptoObject

                        Log.d(tag, "Succeed!!")
                    }
                })
        }
    }

    private fun createPromptInfo() {
        val promptBuilder = PromptInfo.Builder().apply {
            setTitle("Biometric login for my application")
            setSubtitle("Log in using your biometric credential")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }
        promptInfo = promptBuilder.build()
    }

    private fun requestAuthenticate() = with(binding) {
        val biometricManager = BiometricManager.from(this@BiometricActivity)

        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d(tag, "Application can authenticate using biometric features.")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.d(tag, "There is no biometric features on this device.")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.d(tag, "Biometric features are currently unavailable.")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d(tag, "Application needs a credentials to use biometric features.")

                showAuthenticateAlert()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                TODO()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                TODO()
            }
        }

        // If is there any problem, it'll launch for biometric authentication.
        runAuthentication()
    }

    private fun runAuthentication() {
        Log.d(tag, "[Variables]: runAuthentication= $promptInfo")

        promptInfo?.let {
            biometricPrompt?.authenticate(it)
        }
    }

    private fun showAuthenticateAlert() {
        val dialogBuilder = AlertDialog.Builder(this@BiometricActivity).apply {
            setTitle("테스트 앱")
            setMessage("지문 등록이 필요합니다. 지문 등록 설정 화면으로 이동하시겠습니까?")
            setPositiveButton("확인") { _, _ ->
                showBiometricSettings()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
        }
        dialogBuilder.show()
    }

    private fun showBiometricSettings() {
        val enrollIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                putExtra(
                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                )
            }
        } else {
            TODO("VERSION.SDK_INT < R")
        }
        requestLauncher.launch(enrollIntent)
    }

    companion object {
        private const val tag = "BiometricActivity"
    }
}