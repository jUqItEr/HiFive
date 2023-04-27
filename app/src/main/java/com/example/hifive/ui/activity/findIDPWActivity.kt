package com.example.hifive.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import com.example.hifive.databinding.ActivityFindIdpwBinding

class findIDPWActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindIdpwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idRadioButton.isChecked = true
        binding.findEmailAddress.isVisible = false

        binding.findSendButton.setOnClickListener {
            binding.findSendButton.text = "재전송"
        }

        binding.findVerifyButton.setOnClickListener {

        }

        binding.idRadioButton.setOnClickListener{
            binding.findEmailAddress.isVisible = false
        }

        binding.pwRadioButton.setOnClickListener{
            binding.findEmailAddress.isVisible = true
        }
    }
}