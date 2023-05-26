package com.example.hifive.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.adapters.ViewBindingAdapter.setPadding
import com.example.hifive.R
import com.example.hifive.data.RetrofitClient
import com.example.hifive.data.model.SpentListRequest
import com.example.hifive.databinding.ActivityMainBinding
import com.example.hifive.ui.base.BaseActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private var bundle: Bundle? = null
    private var backPressedTime: Long = 0
    private val BACK_KEY_TIME_INTERVAL: Long = 2000 // 2초
    private val cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH) + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = intent.extras?.getBundle("user")
        Log.d("bundle data(main)", "${bundle?.getString("name")}")
        initComponents()

    }
    override fun onBackPressed() {
        if (backPressedTime + BACK_KEY_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            finishAffinity() // 앱의 모든 엑티비티 종료
        } else {
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }


    private fun initComponents() = with(binding) {

        var money: Int?

        // 월 사용내역 호출
        CoroutineScope(Dispatchers.IO).launch {
            money = bundle?.getString("id")
                ?.let { SpentListRequest(it, year.toString(), month.toString()) }
                ?.let { RetrofitClient.requestTotalPrice(it) }

            launch(Dispatchers.Main) {
                if (bundle != null) {
                    mothlyInfo.text = bundle!!.getString("name")
                        .toString() + "님\n" + "${year}년 ${month}월 사용내역\n" + "${money}원"
                }
            }
        }

        // 로그아웃
        logoutButton.setOnClickListener{
            //logout()
            // todo user 정보 clear 함수 추가
            finish()
        }

        // 월별 내역조회 버튼
        binding.button1.setOnClickListener{
            val intent: Intent = Intent(this@MainActivity, MonthlyListActivity::class.java)
            intent.putExtra("user", bundle)
            startActivity(intent)
        }

        // 카드 정보, 등록 버튼
//        button2.setOnClickListener{
//
//        }

        //카드 등록
        binding.button4.setOnClickListener{
            val intent=Intent(this@MainActivity,CardRegistActivity::class.java)
            intent.putExtra("user",bundle)
            startActivity(intent)
        }

        //회원탈퇴
        withdrawalButton.setOnClickListener {
            materialNegativePositiveDialog("${bundle?.getString("id")}")
        }
    }

    //계정 삭제 password 확인 다이얼로그
    private fun materialNegativePositiveDialog(id: String) {
        val passwordEditText = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            setPadding(10, 10, 10, 10) // EditText의 패딩값을 초기화해줍니다.
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val container = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(50, 50, 50, 50) // LinearLayout에 패딩 값을 추가합니다.
            addView(passwordEditText)
        }


        MaterialAlertDialogBuilder(this)
            .setTitle("회원 탈퇴")
            .setMessage("탈퇴를 원하시면 비밀번호를 입력해 주세요\n(다시는 복구할 수 없습니다.)")
            .setView(container)
            .setNegativeButton("취소") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("회원 탈퇴") { dialog, which ->
                // password 비암호화 전송
                bundle?.getString("id")
                    ?.let { RetrofitClient.deleteUser(this, it, passwordEditText.text.toString()) }
                //todo password 암호화 전송
                //RetrofitClient.deleteUser(this, id, encrypt(passwordEditText.text.toString()))
            }
            .show()
    }

    //password 암호화 함수
    private fun encrypt(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val tag = "MainActivity"
    }
}