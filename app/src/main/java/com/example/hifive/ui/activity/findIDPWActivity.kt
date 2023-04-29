package com.example.hifive.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.hifive.data.RetrofitClient
import com.example.hifive.databinding.ActivityFindIdpwBinding
import java.security.MessageDigest

class findIDPWActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindIdpwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idRadioButton.isChecked = true
        binding.findEmailAddress.isVisible = false

        binding.findSendButton.setOnClickListener {
            //전송버튼 action
            //todo - 서버 테스트
            binding.findSendButton.text = "재전송"
            binding.findVerifyButton.isEnabled = true
            binding.Findverify.isEnabled = true
            if(binding.idRadioButton.isSelected){
                //id-코드요청
                val name = binding.findName.text.toString()
                val phone = binding.findPhone.text.toString()
                RetrofitClient.requestAuth(this@findIDPWActivity, name, phone)
            } else{
                //pw-코드요청
                val name = binding.findEmailAddress.text.toString()
                val phone = binding.findPhone.text.toString()
                val id = binding.findEmailAddress.text.toString()
                RetrofitClient.requestPwAuth(this@findIDPWActivity, id, name, phone)
            }
        }

        binding.findVerifyButton.setOnClickListener {
            //인증코드 검증버튼 action
            if(binding.idRadioButton.isSelected){
                //id 찾기
                //todo 검증 성공시 아이디 출력
            } else{
                //pw 찾기
                //todo 검증 성공여부
                //검증 성공
                if(true){
                    binding.findName.isVisible = false
                    binding.findPhone.isVisible = false
                    binding.Findverify.isVisible = false
                    binding.idRadioButton.isEnabled = false
                    binding.pwRadioButton.isEnabled = false
                    binding.findSendButton.isVisible = false
                    binding.findVerifyButton.isVisible = false
                    binding.findEmailAddress.isEnabled = false
                    binding.modiPW.isVisible = true
                    binding.modiPW2.isVisible = true
                    binding.modiButton.isVisible = true
                }
            }
        }

        binding.modiButton.setOnClickListener{
            if(binding.modiPW.text.toString().equals(binding.modiPW2.text.toString())) {
                val id = binding.findEmailAddress.text.toString()
                val pwd = encrypt(binding.modiPW.text.toString())
                RetrofitClient.changePWD(this@findIDPWActivity, id, pwd)
            } else{
                Toast.makeText(this, "비밀번호를 확인해 주세요", Toast.LENGTH_SHORT)
            }
        }

        binding.idRadioButton.setOnClickListener{
            binding.findEmailAddress.isVisible = false
        }

        binding.pwRadioButton.setOnClickListener{
            binding.findEmailAddress.isVisible = true
        }
    }

    fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}