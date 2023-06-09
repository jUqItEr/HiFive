package com.example.hifive.ui.activity

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.CardRegistRequest
import com.example.hifive.data.model.RegisterRequest
import com.example.hifive.databinding.ActivityCardRegistBinding
import com.example.hifive.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.*

class CardRegistActivity : AppCompatActivity() {
    private var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCardRegistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundle = intent.extras?.getBundle("user")

        binding.userId.setText("${bundle?.getString("id")}")
        binding.userId.isEnabled = false
        binding.EmailAddress.setText("${bundle?.getString("email")}")
        binding.EmailAddress.isEnabled = false

        // 날짜 입력 버튼
        binding.inputDateButton.setOnClickListener(){
            showDatePickerDialog(this@CardRegistActivity, binding.birth)
        }

        // 인증번호 전송 버튼
        binding.sendButton.setOnClickListener {
            val id = binding.userId.text.toString()
            val email = binding.EmailAddress.text.toString()
            val birthday = binding.birth.text.toString()
            Log.d("날짜 확인","$birthday");

            if(sendValid(id, birthday, email)) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = RetrofitClient.requestAuth(email)
                    Log.d("result", "${result}")
                    var success = false
                    if (result != null) {
                        launch(Dispatchers.Main) {
                            showDialog(this@CardRegistActivity, "${result.message}")
                        }
                        success = result.success
                    } else {
                        launch(Dispatchers.Main) {
                            showDialog(this@CardRegistActivity, "요청 실패(NULL)")
                        }
                    }
                    if (success) {
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
                        binding.cardNumber.isEnabled = true
                        binding.expiryValue.isEnabled = true
                        binding.pwd2digitValue.isEnabled = true
                        binding.cardRegistBtn.isEnabled=true
                        binding.cardNumber.isVisible = true
                        binding.expiryValue.isVisible = true
                        binding.pwd2digitValue.isVisible = true
                        binding.cardRegistBtn.isVisible=true

                        binding.inputDateButton.isEnabled = false
                        binding.userId.isEnabled = false
                        binding.EmailAddress.isEnabled = false
                        binding.verifyNumber.isEnabled = false
                        binding.verifyButton.isEnabled = false
                        binding.sendButton.isEnabled = false

                        showDialog(this@CardRegistActivity, "${message}")
                    }
                } else {
                    // 검증 실패시
                    launch(Dispatchers.Main) {
                        showDialog(this@CardRegistActivity, "${message}")
                    }
                }
            }
        }

        // todo 카드등록 코드 수정
        // 카드등록 버튼
        binding.cardRegistBtn.setOnClickListener {
            val card_number= binding.cardNumber.text.toString()
            val expiry = binding.expiryValue.text.toString()
            val birth = binding.birth.text.toString().substring(2)
            Log.d("생년월일 체크","$birth")
            val pwd_2digit=binding.pwd2digitValue.text.toString()
            val id = binding.userId.text.toString()
            val card_name=""

            if(isValid(id, birth, card_number,expiry,pwd_2digit,card_name)) {
                CoroutineScope(Dispatchers.IO).launch {
                    var message = "카드 등록 실패"
                    if (RetrofitClient.cardRetist(this@CardRegistActivity, CardRegistRequest(card_number,expiry,birth,pwd_2digit,id,card_name,certification = true))) {
                        message = "카드등록 성공"
                        finish()
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CardRegistActivity, "${message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // 입력 유효값 확인
    private fun sendValid(name: String, birth: String, email: String): Boolean{
        var valid: Boolean = false
        // 이름 확인
        if(name.isNotBlank()){
            valid = true
        } else{
            showDialog(this@CardRegistActivity, "이름을 입력해주세요.")
            return false
        }

        if(birth.isNotBlank()){
            valid = true
        } else{
            showDialog(this@CardRegistActivity, "생년월일을 입력해주세요.")
            return false
        }

        // 이메일 형식 확인
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
        if(email.matches(emailRegex)){
            valid = true
        } else{
            showDialog(this@CardRegistActivity, "이메일 형식에 맞지 않습니다.")
            return false
        }

        return valid
    }

    //유효값 확인
    private fun isValid(id: String, birth:String, card_number:String,expiry:String, pwd_2digit:String,card_name:String): Boolean{
        var valid: Boolean = false

        if(id.isNotBlank()){
            valid = true
        } else{
                showDialog(this@CardRegistActivity, "아이디를 입력해주세요.")
            return false
        }

        if(birth.isNotBlank()) {
            valid = true
        }else {
            showDialog(this@CardRegistActivity, "생년월일을 입력해주세요.")
            return false
        }
        if(card_number.isNotBlank()){
            valid=true
        }else{
            showDialog(this@CardRegistActivity,"카드번호를 입력해주세요.")
            return false
        }
        if(expiry.isNotBlank()){
            valid=true
        }else{
            showDialog(this@CardRegistActivity,"카드 유효기간을 입력해주세요.")
            return false
        }
        if(pwd_2digit.isNotBlank()){
            valid=true
        }else{
            showDialog(this@CardRegistActivity,"카드 비밀번호 앞 두자리를 입력해주세요.")
            return false
        }

        return valid
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