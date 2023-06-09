package com.example.hifive.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.hifive.R
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.FindIdRequest
import com.example.hifive.databinding.ActivityFindIdpwBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class findIDPWActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFindIdpwBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idRadioButton.isChecked = true
        binding.pwRadioButton.isChecked=false;
        binding.findEmailAddress.isVisible = true
        binding.findId.isVisible = false
        binding.idvalue.isVisible=false



        binding.findSendButton.setOnClickListener {
            binding.findSendButton.text = "재전송"
            binding.findVerifyButton.isEnabled = true
            binding.Findverify.isEnabled = true

            val email = binding.findEmailAddress.text.toString()
            val name = binding.findName.text.toString()
            val id = binding.findEmailAddress.text.toString()

            //이메일 전송
            CoroutineScope(Dispatchers.IO).launch {
                var certification = false;
                Log.d("email check","$email")
                val result = RetrofitClient.requestAuth(email)
                if (result != null) {
                    val message = result.message
                    if(result.success) {
                        withContext(Dispatchers.Main) {
                            showDialog(this@findIDPWActivity, "${message}", 1)
                        }
                    }
                }
                Log.d("result test", "$result")
            }
        }

        binding.findVerifyButton.setOnClickListener {
            val email = binding.findEmailAddress.text.toString()
            val name = binding.findName.text.toString()
            val id = binding.findEmailAddress.text.toString()
            //인증코드 검증버튼 action 아이디 찾기
            if (binding.idRadioButton.isChecked) {
                CoroutineScope(Dispatchers.IO).launch {
                    val verify=binding.Findverify.text.toString();
                    var message=""
                    var result=RetrofitClient.requestVerify(email,verify)
                    Log.d("result 체크","$result")

                    if(result?.success==true){
                        val result2=RetrofitClient.requestFindId(FindIdRequest(email,name,certification=true));
                        if (result2?.success ==true) {
                            binding.idvalue.setTextColor(Color.BLUE)
                            message="아이디 : "+result2.id
                        }else{
                            binding.idvalue.setTextColor(Color.RED)
                            message="이름 또는 이메일 틀림"
                        }
                    }else{
                        binding.idvalue.setTextColor(Color.RED)
                        if (result != null) {
                            message=result.message
                        }else{
                            message="에러"
                        }
                    }
                    withContext(Dispatchers.Main) {
                        showDialog(this@findIDPWActivity, message, 0)
                    }
                }
                //비밀번호 찾기
            } else if(binding.pwRadioButton.isChecked){
                CoroutineScope(Dispatchers.IO).launch {
                    val verify=binding.Findverify.text.toString();
                    var result=RetrofitClient.requestVerify(email,verify)
                    Log.d("result 체크","$result")

                    if(result?.success==true){
                        withContext(Dispatchers.Main) {
                            binding.findName.isVisible = false
                            binding.findEmailAddress.isVisible = false
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
                    }else{
                        Log.d("????","??????????")
                    }
                }

            }
        }


        binding.modiButton.setOnClickListener{

            Log.d("아이디","${binding.findId.text.toString()}")
            if(binding.modiPW.text.toString().equals(binding.modiPW2.text.toString())) {
                val id = binding.findId.text.toString()
                val pwd = encrypt(binding.modiPW.text.toString())
                RetrofitClient.changePWD(this@findIDPWActivity, id, pwd, certification = true);
            } else{
                Toast.makeText(this, "비밀번호를 확인해 주세요", Toast.LENGTH_SHORT)
            }
        }

        binding.idRadioButton.setOnClickListener{
            binding.findId.isVisible = false
        }

        binding.pwRadioButton.setOnClickListener{
            binding.findId.isVisible = true
        }
    }

    fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }


    private fun showDialog(activity: Activity, message: String, case: Int){
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
        builder.setPositiveButton("확인") { dialog, _ ->
            // 확인 버튼을 눌렀을 때 수행할 동작
            if(case == 0) {
                activity.finish()
            } else if(case == 1){
                dialog.dismiss()
            }
        }
        builder.create().show()
    }
}