package com.example.hifive.ui.activity

import android.os.Bundle
import com.example.hifive.databinding.ActivityBiometricBinding
import com.example.hifive.ui.base.BaseActivity

class BiometricActivity :
    BaseActivity<ActivityBiometricBinding>(ActivityBiometricBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
    }

    private fun initComponents() = with(binding) {
        
    }

    companion object {
        private const val tag = "BiometricActivity"
    }
}