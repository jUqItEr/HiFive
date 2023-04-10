package com.example.hifive.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.RegisterRequest
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
            val email = binding.EmailAddress.text.toString()
            val pwd = binding.Password.text.toString()
            val pwd2 = binding.Password2.text.toString()
            val name = binding.name.text.toString()
            val personID = "${binding.birth.text.toString()}-${binding.privateID.text.toString()}"

            if(isValid(email, pwd, pwd2, name, personID)) {
                if (RetrofitClient.signUp(this, RegisterRequest(email, pwd, name, personID))) {
                    Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun isValid(email:String, pwd:String, pwd2:String, name:String, personID:String): Boolean{
        var valid: Boolean = false
        if(email.isNotBlank() && pwd.isNotBlank() && name.isNotBlank() && personID.length==14){
            if(pwd.equals(pwd2)){
                valid = true
            } else{
                Toast.makeText(this@SignupActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
        } else{
            Toast.makeText(this@SignupActivity, "빈칸을 작성해 주세요", Toast.LENGTH_SHORT).show()
        }
        return valid
    }
}