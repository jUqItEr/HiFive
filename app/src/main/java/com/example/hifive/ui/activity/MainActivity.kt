package com.example.hifive.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.hifive.databinding.ActivityMainBinding
import com.example.hifive.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initComponents()

    }

    private var backPressedTime: Long = 0
    private val BACK_KEY_TIME_INTERVAL: Long = 2000 // 2초

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
        logoutButton.setOnClickListener{
            //logout()
            // todo user 정보 clear 함수 추가
            finish()
        }
        button1.setOnClickListener{
            val intent: Intent = Intent(this@MainActivity, MonthlyListActivity::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener{

        }
    }

    companion object {
        private const val tag = "MainActivity"
    }
}