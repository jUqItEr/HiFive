package com.example.hifive.ui.activity

import android.os.Bundle
import com.example.hifive.databinding.ActivityLoginBinding
import com.example.hifive.ui.base.BaseActivity

class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()
        initObservers()
    }

    // Init components
    private fun initComponents() {
        with(binding) {

        }
    }

    private fun initObservers() {

    }
}