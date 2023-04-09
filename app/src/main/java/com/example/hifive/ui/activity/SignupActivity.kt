package com.example.hifive.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hifive.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var intent: Intent

        //card reservation
        binding.reservation.setOnClickListener {
            //intent= Intent(this, "")

        }
        binding.signIn.setOnClickListener {
            if(true){
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                finish()
            }
            else{
                Toast.makeText(this, "failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}