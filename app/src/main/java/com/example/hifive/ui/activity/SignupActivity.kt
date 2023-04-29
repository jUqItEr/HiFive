package com.example.hifive.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.RegisterRequest
import com.example.hifive.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var intent: Intent


        binding.sendButton.setOnClickListener {
            //인증번호 전송 action
            //todo - 서버 테스트
            binding.sendButton.text = "재전송"
            binding.verifyButton.isEnabled = true
            binding.verifyNumber.isEnabled = true

            val name = binding.name.text.toString()
            val phone = binding.phone.text.toString()

            RetrofitClient.requestAuth(this@SignupActivity, name, phone)
        }

        // 본인인증
        binding.verifyButton.setOnClickListener {
            //인증번호 검증 action
            if(true) {
                binding.signIn.isEnabled = true
                binding.EmailAddress.isEnabled = true
                binding.Password.isEnabled = true
                binding.Password2.isEnabled = true
            }
        }

        binding.signIn.setOnClickListener {
            val email = binding.EmailAddress.text.toString()
            val pwd = binding.Password.text.toString()
            val pwd2 = binding.Password2.text.toString()
            val name = binding.name.text.toString()
            val phone = "${binding.phone.text}"

            if(isValid(email, pwd, pwd2, name, phone)) {
                CoroutineScope(Dispatchers.IO).launch {
                    var message = "회원가입 실패"
                    if (RetrofitClient.signUp(this@SignupActivity, RegisterRequest(email, encrypt(pwd), name, phone))) {
                        message = "회원가입 성공"
                        finish()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignupActivity, "${message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun isValid(email:String, pwd:String, pwd2:String, name:String, phone:String): Boolean{
        var valid: Boolean = false
        if(email.isNotBlank() && pwd.isNotBlank() && name.isNotBlank() && phone.length>=10){
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

    fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}