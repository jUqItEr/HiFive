package com.example.hifive.ui.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.RegisterRequest
import com.example.hifive.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var intent: Intent

        // 날짜 입력 버튼
        binding.inputDateButton.setOnClickListener(){
            showDatePickerDialog(this@SignupActivity, binding.birth)
        }

        // 인증번호 전송 버튼
        binding.sendButton.setOnClickListener {
            //인증번호 전송 action
            //todo - 서버 테스트
            val name = binding.name.text.toString()
            //val phone = binding.phone.text.toString()
            val email = binding.EmailAddress.text.toString()
            val birthday = binding.birth.text.toString()
            Log.d("날짜 확인","$birthday");

            if(sendValid(name, birthday, email)) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = RetrofitClient.requestAuth(email)
                    Log.d("result", "${result}")
                    var success = false
                    if (result != null) {
                        launch(Dispatchers.Main) {
                            showDialog(this@SignupActivity, "${result.message}")
                        }
                        success = result.success
                    } else{
                        launch(Dispatchers.Main) {
                            showDialog(this@SignupActivity, "요청 실패(NULL)")
                        }
                    }

                    if(success){
                        launch(Dispatchers.Main) {
                            binding.sendButton.text = "재전송"
                            binding.verifyButton.isEnabled = true
                            binding.verifyNumber.isEnabled = true
                        }
                    }
                }
            }
        }

        // todo server test
        // 인증번호 검증 버튼(본인인증)
        binding.verifyButton.setOnClickListener {
            //인증번호 검증 action
            val email = "${binding.EmailAddress.text}"
            val verify = "${binding.verifyNumber.text}"

            CoroutineScope(Dispatchers.IO).launch {
                val result = RetrofitClient.requestVerify(email, verify)
                var success = false
                var message = ""

                if(result != null){
                    success = result.success
                    message = result.message
                }

                if (success) {
                    // 검증 성공시
                    launch(Dispatchers.Main) {
                        binding.signIn.isEnabled = true
                        binding.id.isEnabled = true
                        binding.Password.isEnabled = true
                        binding.Password2.isEnabled = true
                        binding.signIn.isVisible = true
                        binding.id.isVisible = true
                        binding.Password.isVisible = true
                        binding.Password2.isVisible = true

                        binding.inputDateButton.isEnabled = false
                        //binding.phone.isEnabled = false
                        binding.name.isEnabled = false
                        binding.EmailAddress.isEnabled = false
                        binding.verifyNumber.isEnabled = false
                        binding.verifyButton.isEnabled = false
                        binding.sendButton.isEnabled = false

                        showDialog(this@SignupActivity, "${message}")
                    }
                } else {
                    // 검증 실패시
                    launch(Dispatchers.Main) {
                        showDialog(this@SignupActivity, "${message}")
                    }
                }
            }
        }

        // todo 회원가입 코드 수정
        // 회원가입 버튼
        binding.signIn.setOnClickListener {
            val id = binding.id.text.toString()
            val email = binding.EmailAddress.text.toString()
            val pwd = binding.Password.text.toString()
            val pwd2 = binding.Password2.text.toString()
            val name = binding.name.text.toString()
            //val phone = "${binding.phone.text}"
            val birth = "${binding.birth.text}"


            if(isValid(id, pwd, pwd2)) {
                CoroutineScope(Dispatchers.IO).launch {
                    var message = "회원가입 실패"
                    if (RetrofitClient.signUp(this@SignupActivity, RegisterRequest(id, encrypt(pwd),email, name,birth, certification = true))) {
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

    // name, phone, email 유효값 확인
    private fun sendValid(name: String, birth: String, email: String): Boolean{
        var valid: Boolean = false
        // 이름 확인
        if(name.isNotBlank()){
            valid = true
        } else{
            showDialog(this@SignupActivity, "이름을 입력해주세요.")
            return false
        }

        if(birth.isNotBlank()){
            valid = true
        } else{
            showDialog(this@SignupActivity, "생년월일을 입력해주세요.")
            return false
        }

        // 휴대전화 형식 확인
//        val phoneRegex = Regex("^010[0-9]{7,8}$")
//        if(phone.matches(phoneRegex)){
//            valid = true
//        } else{
//            showDialog(this@SignupActivity, "휴대폰 형식에 맞지 않습니다.")
//            return false
//        }

        // 이메일 형식 확인
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        Log.d("email match","${emailRegex}, ${email}, ${email.matches(emailRegex)}")
        if(email.matches(emailRegex)){
            valid = true
        } else{
            showDialog(this@SignupActivity, "이메일 형식에 맞지 않습니다.")
            return false
        }

        return valid
    }

    // id, pwq 유효값 확인
    private fun isValid(id: String, pwd:String, pwd2:String): Boolean{
        var valid: Boolean = false

        if(id.isNotBlank()){
            valid = true
        } else{
            showDialog(this@SignupActivity, "아이디를 입력해주세요.")
            return false
        }

        if(pwd.isNotBlank()){
            if(pwd.equals(pwd2)){
                valid = true
            } else{
                showDialog(this@SignupActivity, "비밀번호가 일치하지 않습니다.")
                return false
            }
        } else{
            showDialog(this@SignupActivity, "비밀번호를 입력해 주세요.")
            return false
        }

        return valid
    }

    private fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // 다이얼로그
    private fun showDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setPositiveButton("확인") { dialog, _ ->
            // 확인 버튼을 눌렀을 때 수행할 동작
            dialog.dismiss() // 다이얼로그 닫기
        }
        builder.create().show()
    }

    // 날짜 입력 다이얼로그
    private fun showDatePickerDialog(context: Context, editText: EditText) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d%02d%02d", year, month + 1, dayOfMonth)
                editText.setText(selectedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }
}